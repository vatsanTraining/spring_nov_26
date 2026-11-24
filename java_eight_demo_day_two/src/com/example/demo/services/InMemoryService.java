package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.ifaces.MyService;
import com.example.demo.model.Policy;

public class InMemoryService implements MyService<Policy> {
	
	
	private List<Policy> list;
	
	

	public InMemoryService() {
		super();
		
		this.list= new ArrayList<>();
	}

	@Override
	public boolean add(Policy t) {
		
		return this.list.add(t);
	}

	@Override
	public List<Policy> findAll() {
		
		return this.list;
	}

}
