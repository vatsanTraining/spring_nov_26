package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PolicyDto;
import com.example.demo.model.Policy;
import com.example.demo.services.PolicyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/policy")
public class PolicyController {

	
private PolicyService service;
	

	
	public PolicyController(PolicyService service) {
		super();
		this.service=service;
	}
	
	
	

	@PostMapping
	public ResponseEntity<PolicyDto> add(@Valid @RequestBody PolicyDto policy) {
		
		PolicyDto saved = this.service.add(policy);
		
		return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(saved);
	}

	@GetMapping
	public ResponseEntity<List<PolicyDto>> findAll() {
		
		return ResponseEntity.ok(this.service.findAll());
	}

	
}
