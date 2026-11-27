package com.example.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.demo.enums.PolicyType;
import com.example.demo.model.Policy;
import com.example.demo.model.PolicyHandler;

@SpringBootApplication
public class FirstSpringProjectDayTwoApplication {

	public static void main(String[] args) {
	
		ApplicationContext ctx=SpringApplication.run(FirstSpringProjectDayTwoApplication.class, args);
	
		
//		MyService<Policy> service = new InMemoryService();
//		
//		PolicyHandler handler = new PolicyHandler(service);
//		
		
		
		 PolicyHandler handler = ctx.getBean(PolicyHandler.class);
		 
		 
		 Policy one = ctx.getBean("first",Policy.class);
		 
		 Policy two = ctx.getBean("second",Policy.class);
		 
		 Policy three = ctx.getBean("third",Policy.class);
		 
		
		 handler.add(one);
		 handler.add(two);
		 handler.add(three);
		 
		 handler.findAll().forEach(System.out::println);
	}
	
	
	
	
	
	
	
	@Bean
	Policy first() {
		
		return new Policy(100032L, "Ram",PolicyType.Endowment, 100000);
	}
	
	@Bean
	Policy second() {
		
		return new Policy(200032L, "Suresh",PolicyType.Medical, 200000);
	}
	
	@Bean
	Policy third() {
		
		return new Policy(300032L, "Magesh",PolicyType.TermLife, 300000);
	}

}
