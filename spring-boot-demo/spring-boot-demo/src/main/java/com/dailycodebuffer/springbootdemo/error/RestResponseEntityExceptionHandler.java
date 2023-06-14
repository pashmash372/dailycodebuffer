package com.dailycodebuffer.springbootdemo.error;

import com.dailycodebuffer.springbootdemo.model.ErrorMessage;
import com.dailycodebuffer.springbootdemo.service.EmployeeService;
//import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage employeeNotFoundHandler(EmployeeNotFoundException exception ){
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, "Employee not found");
        return errorMessage;
    }
}
