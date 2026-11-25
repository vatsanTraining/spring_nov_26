package com.example.demo.ifaces;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Policy;

public interface PolicyRepoistory extends JpaRepository<Policy, Long> {

	
}
