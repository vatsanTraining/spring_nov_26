package com.example.demo.dto;


import com.example.demo.enums.PolicyType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PolicyDto(
		long policyNumber,
		@NotEmpty
		@Size(max = 12,min = 3,message = "Policy Holder Name should be 3 to 12 chars")
		String policyHolder,
		PolicyType policyType,
		 double premiumAmount
		) {

}
