package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.PolicyDto;
import com.example.demo.ifaces.PolicyRepoistory;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.model.Policy;

@Service
public class PolicyService {


	private PolicyMapper mapper;
	private PolicyRepoistory repo;

	public PolicyService(PolicyRepoistory repo,PolicyMapper mapper) {
		super();
		this.repo = repo;
		this.mapper = mapper;
		System.out.println(repo.getClass().getName());
	}
	
	
	public PolicyDto add(PolicyDto t) {
		
		
		Policy saved = this.repo.save(mapper.toEntity(t));
		
		return mapper.toDto(saved);
		
	}

	public List<PolicyDto> findAll(){
		
		List<Policy> found = this.repo.findAll();
		
		
		return found.stream().map(mapper::toDto).toList();
	}
	
	
	public List<PolicyDto> findByPolicyHolder(String name){
		
		
      List<Policy> found = this.repo.findByPolicyHolder(name);
		
		
		return found.stream().map(mapper::toDto).toList();
	}
	
	
	public List<PolicyDto> findByPremium(){
		
		
	      List<Policy> found = this.repo.getMeByPremiumAmount();
			
			
			return found.stream().map(mapper::toDto).toList();
		}


	public void  remove(long id) {
	
		if(!this.repo.existsById(id)) {
			
			throw new RuntimeException("Element with id "+id+ "Not Present");
		}else {
		this.repo.deleteById(id);
		}
	}
		

}


