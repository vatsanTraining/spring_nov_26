package com.example.demo;

import com.example.demo.enums.PolicyStatus;
import com.example.demo.service.FileHandlingService;

public class FileApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		   String file = "policy_status.txt";
		   
		   FileHandlingService service = new FileHandlingService();

	        service.writeStatus(PolicyStatus.ACTIVE, file);

	        PolicyStatus loadedStatus = service.readStatus(file);
	        System.out.println("Loaded from file: " + loadedStatus);

	}

}
