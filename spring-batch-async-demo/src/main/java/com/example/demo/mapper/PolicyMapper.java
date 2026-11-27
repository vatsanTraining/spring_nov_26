package com.example.demo.mapper;


import org.springframework.stereotype.Component;

import com.example.demo.dto.PolicyDto;
import com.example.demo.model.Policy;

@Component
public class PolicyMapper {

	
	public Policy  toEntity(PolicyDto dto) {
		
		return new Policy(dto.policyNumber(),dto.policyHolder(),
				dto.policyType(), dto.premiumAmount());
	}
	
	public PolicyDto toDto(Policy entity) {
		
		return new PolicyDto(entity.getPolicyNumber(),
				entity.getPolicyHolder(), entity.getPolicyType(), 
				entity.getPremiumAmount());
		
		
	}
}
