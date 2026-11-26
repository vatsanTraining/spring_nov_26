package com.example.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Policy Mangement",version = "1.0"))
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
