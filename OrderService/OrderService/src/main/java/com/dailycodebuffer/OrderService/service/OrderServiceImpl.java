package com.dailycodebuffer.OrderService.service;

import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.client.PaymentService;
import com.dailycodebuffer.OrderService.external.client.ProductService;
import com.dailycodebuffer.OrderService.external.request.PaymentRequest;
import com.dailycodebuffer.OrderService.external.response.PaymentResponse;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import com.dailycodebuffer.ProductService.model.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements  OrderService{

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public long placeOrder(OrderRequest orderRequest) {

        // Order Entity -> Save the data with Status Order Created
        // Product Service -> Block Products (Reduce the quantity)
        // Payment Service -> Payments -> Success -> COMPLETE,
        // Else -> CANCELLED

        log.info("Placing Order Request: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), (int) orderRequest.getQuantity());
        log.info("Creating Order with Status CREATED");

        Order order= Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling Payment Service to do Payment");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully." +
                    " Changing the order status to PLACED {}", order.getId());
            orderStatus = "PLACED";
        }
        catch (Exception e){
            log.error("Error occured in payment. " +
                    "Changing order status to PAYMENT_FAILED", e.getMessage());
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);


        log.info("Order Places successfully with Order Id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for Order Id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new CustomException(
                        "Order not found for Id: "+ orderId,
                        "NOT_FOUND",
                        404
                ));

        log.info("Invoking Product service to fetch the product for id: {}", order.getId());

         ProductResponse productResponse = restTemplate
                .getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
                        ProductResponse.class
                );

         log.info("Getting payment information from the payment service");

        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                        "http://PAYMENT-SERVICE/payment/order/"+order.getId(),
                        PaymentResponse.class
                );


        OrderResponse.ProductDetails productDetails
                = OrderResponse.ProductDetails.builder()
             .productId(productResponse.getProductId())
             .productName(productResponse.getProductName())
             .build();

        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();

        OrderResponse orderResponse
                = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;
    }
}
