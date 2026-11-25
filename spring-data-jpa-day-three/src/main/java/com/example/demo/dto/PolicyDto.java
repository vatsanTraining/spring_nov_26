package com.example.demo.dto;

import org.hibernate.validator.constraints.Range;

import com.example.demo.enums.PolicyType;

import jakarta.validation.constraints.NotEmpty;

public record PolicyDto(
		long policyNumber,
		@NotEmpty
		@Range(max = 12,min = 3,message = "Policy Holder Name should be 3 to 12 chars")
		String policyHolder,
		PolicyType policyType,
		 double premiumAmount
		) {

}
