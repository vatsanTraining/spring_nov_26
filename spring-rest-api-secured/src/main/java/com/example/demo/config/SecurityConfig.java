package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	
	@Bean
	BCryptPasswordEncoder encoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Throwable{
		
		http
		.csrf(csrf -> csrf.disable()) 
		.authorizeHttpRequests(auth -> auth
		.requestMatchers("/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/actuator/**").permitAll()
		.anyRequest().authenticated()
		)
		.httpBasic(Customizer.withDefaults());
		
		return http.build();
	}
	

	
	
	@Bean
	UserDetailsService  userDetails(BCryptPasswordEncoder encoder) {
		
		UserDetails adminUser =User.builder().username("india")
				.password(encoder.encode("india")).roles("ADMIN").build();
		
		UserDetails guestUser =User.builder().username("nepal")
				.password(encoder.encode("nepal")).roles("GUEST").build();
		
		return new InMemoryUserDetailsManager(adminUser,guestUser);
		
		
	}
}
