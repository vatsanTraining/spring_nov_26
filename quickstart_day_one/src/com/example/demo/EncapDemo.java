package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EncapDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		Agent ram = new InsuranceAgent();
		
		//ram.name="Ramesh";
		
		System.out.println(ram);
		
		try {
			ram.setId((byte)67);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ram.setName("Ramakrishna");
		
		System.out.println(ram.getName());
		
		
		Agent shyam = new InsuranceAgent((byte) 111, "Shyam", 4.5,"BrachManager");
		
//		System.out.println(shyam.getName());
//		System.out.println(shyam.getCommission());
//		System.out.println(shyam.getId());
//		
		
		System.out.println(shyam);

		
		BigDecimal dem = new BigDecimal("45.2352");
		
		var resp=dem.setScale(3,RoundingMode.UP);
		
		System.out.println("Rouded Value "+resp);
		
		BigDecimal balance =new BigDecimal("1500.00");
        BigDecimal feeRate = new BigDecimal("0.025");
        BigDecimal fee = balance.multiply(feeRate)
                                .setScale(2, RoundingMode.HALF_UP); 
        
        System.out.println(fee);


	}

}
