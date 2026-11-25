package com.example.demo;

import java.beans.BeanProperty;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.enums.PolicyType;
import com.example.demo.model.Policy;
import com.example.demo.services.PolicyService;

@SpringBootApplication
public class SpringDataJpaDayThreeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJpaDayThreeApplication.class, args);
	}

}
//	@Bean
//	CommandLineRunner runner(PolicyService service) {
//		
//		return args ->{
//			
//			service.add(new Policy(3000033L,"Harish",PolicyType.TermLife,80000));
//
//		};
//	}
//}
	

//	@Bean
//	CommandLineRunner runner(PolicyService service) {
//		
//		return new CommandLineRunner() {
//			
//			@Override
//			public void run(String... args) throws Exception {
//				// TODO Auto-generated method stub
//				
//			}
//		};
//	}
//}
