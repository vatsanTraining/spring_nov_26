package com.example.demo.ifaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Policy;

public interface PolicyRepoistory extends JpaRepository<Policy, Long> {

	
}
