package com.example.demo.advice;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionAdvice {

	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(BindException ex, WebRequest request){
		
		Map<String, String> errors = new LinkedHashMap<>();

		ex.getBindingResult().getFieldErrors().forEach(error -> 
		
		errors.put(error.getField(), error.getDefaultMessage()));
		
		
		errors.put("URI", request.getDescription(false));
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

		
		
	}
}
