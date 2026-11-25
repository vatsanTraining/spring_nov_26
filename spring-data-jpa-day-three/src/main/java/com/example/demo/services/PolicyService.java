package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.ifaces.PolicyRepoistory;
import com.example.demo.model.Policy;

@Service
public class PolicyService {

	
	private PolicyRepoistory repo;

	public PolicyService(PolicyRepoistory repo) {
		super();
		this.repo = repo;
	}
	
	
	public Policy add(Policy t) {
		
		
		return this.repo.save(t);
	}

	public List<Policy> findAll(){
		
		return this.repo.findAll();
	}

}


