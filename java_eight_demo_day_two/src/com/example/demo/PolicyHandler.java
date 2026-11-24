package com.example.demo;

import java.util.List;

import com.example.demo.ifaces.MyService;

public class PolicyHandler {

	
	private MyService<Policy> service;
	

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
