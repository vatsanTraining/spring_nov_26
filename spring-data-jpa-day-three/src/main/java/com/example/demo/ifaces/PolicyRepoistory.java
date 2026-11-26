package com.example.demo.ifaces;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Policy;

public interface PolicyRepoistory extends JpaRepository<Policy, Long> {

	
	  List<Policy> findByPolicyHolder(String policyHolder);
	  

	  @Query(value = "Select * from policy_2026 where premium_amount>10000",nativeQuery = true)
	  List<Policy> getMeByPremiumAmount();
	
}
