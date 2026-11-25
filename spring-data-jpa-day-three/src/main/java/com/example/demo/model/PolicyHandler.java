package com.example.demo.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.demo.ifaces.PolicyRepoistory;
import com.example.demo.services.PolicyService;

@Service
public class PolicyHandler {


	
	private PolicyService service;
	

	
	public PolicyHandler(PolicyService service) {
		super();
		this.service=service;
	}
	
	
	

	public Policy add(Policy policy) {
		
		return this.service.add(policy);
	}
	
	public List<Policy> findAll() {
		
		return this.service.findAll();
	}
	
	
	
}
