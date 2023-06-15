package com.dailycodebuffer.OrderService.service;

import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.external.client.PaymentService;
import com.dailycodebuffer.OrderService.external.client.ProductService;
import com.dailycodebuffer.OrderService.external.response.PaymentResponse;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.model.PaymentMode;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import com.dailycodebuffer.ProductService.model.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Successfully() {
        // Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                                ProductResponse.class))
                .thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/"+order.getId(),
                PaymentResponse.class))
                .thenReturn(getMockPaymentResponse());

        // Actual
        OrderResponse orderResponse = orderService.getOrderDetails(1L);

        // Verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class);
        verify(restTemplate, times(1)).getForObject(
                "http://PAYMENT-SERVICE/payment/order/"+order.getId(),
                PaymentResponse.class);

        // Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(),orderResponse.getOrderId());

    }


    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1)
                .amount(100)
                .quantity(200)
                .productId(2)
                .build();
    }

    private ProductResponse getMockProductResponse() {

        return ProductResponse.builder()
                .productId(2)
                .productName("iPhone")
                .price(100)
                .quantity(200)
                .build();
    }


    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }
}