package com.example.demo.model;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.ifaces.MyService;

@Component
public class PolicyHandler {


	
	private MyService<Policy> service;
	

//	public PolicyHandler(@Qualifier("fileService") MyService<Policy> service) {
//		super();
//		this.service=service;
//	}
//	
	
	
	public PolicyHandler(MyService<Policy> service) {
		super();
		this.service=service;
	}
	
	
	

	public boolean add(Policy policy) {
		
		return this.service.add(policy);
	}
	
	public List<Policy> findAll() {
		
		return this.service.findAll();
	}
	
	
	
}
