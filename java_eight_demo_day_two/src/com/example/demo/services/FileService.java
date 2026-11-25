package com.example.demo.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.enums.PolicyType;
import com.example.demo.ifaces.MyService;
import com.example.demo.model.Policy;


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

		var list = new ArrayList<Policy>();
		
		
		try(BufferedReader reader = new BufferedReader(new FileReader(new File("policy.csv")))){
			
			
			String line =null;
			
			while( (line =reader.readLine())!=null){
				
				String[] values = line.split(",");
				
				
				Policy obj  = new Policy(Long.parseLong(values[0]),
						           values[1],PolicyType.valueOf(values[3]), Double.parseDouble(values[2]));
				
				list.add(obj);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}

