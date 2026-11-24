package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.example.demo.enums.PolicyType;

class Trial implements Consumer<Policy>{

	@Override
	public void accept(Policy t) {

		System.out.println(t);
	}
	
	
}

public class Application {

	public static void main(String[] args) {

		
		Policy policyOne = new Policy(10230230, "Ramesh", PolicyType.Endowment, 100000.00);
		Policy policyTwo = new Policy(20230230, "Anand", PolicyType.TermLife, 200000.00);
		Policy policyThree = new Policy(30230230, "Shiv", PolicyType.Endowment, 300000.00);
		Policy policyFour = new Policy(40230230, "Zahir", PolicyType.Medical, 400000.00);

		Policy[] polArry = {policyOne,policyTwo,policyThree,policyFour};
		

		List<Policy> policyList = new ArrayList<>();
		
		policyList.add(policyThree);
		policyList.add(policyOne);
		policyList.add(policyFour);
		policyList.add(policyTwo);
		
		
		//policyList.forEach(e -> System.out.println(e));
		
		policyList.forEach(System.out::println);
		
		Predicate<Double> calculate = e -> e >200000.00 ;
		
		
		policyList.forEach(e -> { 
		
			if(calculate.test(e.getPremiumAmount())) {
				
			System.out.println(e +" => Listed for Discount");
			} else {
				System.out.println(e+ "==> NOT Listed for Discount");
			}
		});
		
		
		
		
	}

}
