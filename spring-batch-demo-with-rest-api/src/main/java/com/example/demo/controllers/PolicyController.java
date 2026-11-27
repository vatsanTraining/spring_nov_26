package com.example.demo.controllers;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping(path = "/api/v1/batch")
//@Profile("file")
public class PolicyController {

	
	 private final JobOperator jobOperator;

	    private final Job importPolicyJob;

	    public PolicyController(JobOperator jobOperator, Job importPolicyJob) {
	        this.jobOperator = jobOperator;
	        this.importPolicyJob = importPolicyJob;
	    }

	    @PostMapping("/import-policies")
	    public ResponseEntity<String> runBatchJob() throws Exception {
	        JobParameters params = new JobParametersBuilder()
	                .addLong("startAt", System.currentTimeMillis())
	                .toJobParameters();
	        jobOperator.run(importPolicyJob, params);
	        return ResponseEntity.ok("Batch job started successfully!");
	    }
	    
	   
	    @PostMapping("/upload-policies")
	    public ResponseEntity<String> uploadCsvWithSummary(@RequestParam MultipartFile file) {
	        if (file.isEmpty()) {
	            return ResponseEntity.badRequest().body("Please upload a non-empty CSV file.");
	        }

	        try {
	            Path tempFile = Files.createTempFile("policies-", ".csv");
	            file.transferTo(tempFile.toFile());

	            JobParameters params = new JobParametersBuilder()
	                    .addString("filePath", tempFile.toAbsolutePath().toString())
	                    .addLong("startAt", System.currentTimeMillis())
	                    .toJobParameters();

	            JobExecution execution = jobOperator.run(importPolicyJob, params);

	            Files.deleteIfExists(tempFile);
	            
	            StepExecution stepExecution = execution.getStepExecutions().iterator().next();

	            String summary = String.format(
	                    "Status: %s | Read: %d | Saved: %d | Filtered (Duplicates/Invalid): %d | Skipped (Parse Errors): %d",
	                    execution.getStatus(),
	                    stepExecution.getReadCount(),
	                    stepExecution.getWriteCount(),
	                    stepExecution.getFilterCount(), 
	                    stepExecution.getSkipCount()    
	                    
	            );

	            return ResponseEntity.ok(summary);



	        } catch (Exception e) {
	            return ResponseEntity.internalServerError()
	                    .body("Failed to process CSV: " + e.getMessage());
	        }
	    }


	}

	

