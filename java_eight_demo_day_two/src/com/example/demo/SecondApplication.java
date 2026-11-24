package com.example.demo;

import java.util.List;

import com.example.demo.enums.PolicyType;
import com.example.demo.ifaces.MyService;
import com.example.demo.services.InMemoryService;

public class SecondApplication {

	public static void main(String[] args) {

		Policy policyOne = new Policy(10230230, "Ramesh", PolicyType.Endowment, 100000.00);
		Policy policyTwo = new Policy(20230230, "Anand", PolicyType.TermLife, 200000.00);
		Policy policyThree = new Policy(30230230, "Shiv", PolicyType.Endowment, 300000.00);
		Policy policyFour = new Policy(40230230, "Zahir", PolicyType.Medical, 400000.00);

		
		MyService<Policy> service = new InMemoryService();
		
		PolicyHandler handler = new PolicyHandler(service);
		
		System.out.println("is Added =>"+handler.add(policyOne));
		System.out.println("is Added =>"+handler.add(policyTwo));
		System.out.println("is Added =>"+handler.add(policyThree));
		System.out.println("is Added =>"+handler.add(policyFour));
		
		
		List<Policy> policyList = handler.findAll();
		
		
		policyList.forEach(System.out::println);

	}

}
