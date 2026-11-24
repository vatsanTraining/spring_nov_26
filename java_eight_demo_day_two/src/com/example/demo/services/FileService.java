package com.example.demo.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.example.demo.Policy;
import com.example.demo.ifaces.MyService;

public class FileService implements MyService<Policy> {

	@Override
	public boolean add(Policy t) {

		boolean isAdded = false;
		
		
		try(PrintWriter writer = new PrintWriter(new FileWriter(new File("policy.csv"), true))){
			
			writer.println(t);
			
			isAdded=true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return isAdded;
	}

	@Override
	public List<Policy> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
