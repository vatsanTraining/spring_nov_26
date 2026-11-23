package com.example.demo.service;

import com.example.demo.enums.PolicyStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileHandlingService {


	    public  void writeStatus(PolicyStatus status, String filePath) {
	        try (PrintWriter writer = new PrintWriter(filePath)) {
	            writer.println(status.name()); // Writes "ACTIVE", "MATURED", or "LAPSED"
	            System.out.println("Saved status: " + status);
	        } catch (FileNotFoundException e) {
	            System.err.println("Error writing to file: " + e.getMessage());
	        }
	    }

	    public  PolicyStatus readStatus(String filePath) {
	        try (Scanner scanner = new Scanner(new File(filePath))) {
	            if (scanner.hasNextLine()) {
	                String statusText = scanner.nextLine().trim();
	                return PolicyStatus.valueOf(statusText); // Converts String back to Enum
	            }
	        } catch (FileNotFoundException e) {
	            System.err.println("File not found: " + e.getMessage());
	        } catch (IllegalArgumentException e) {
	            System.err.println("Invalid status value in file: " + e.getMessage());
	        }
	        return null;
	    }

	

}
