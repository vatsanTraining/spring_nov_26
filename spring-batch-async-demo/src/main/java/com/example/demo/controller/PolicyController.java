package com.example.demo.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/v1/batch")
public class PolicyController {

    private final JobOperator asyncJobOperator;
    private final Job importPolicyJob;
    private final JobRepository jobExplorer;
    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    public PolicyController(
            @Qualifier("asyncJobOperator") JobOperator asyncJobOperator,
            Job importPolicyJob,
            JobRepository jobExplorer) {
        this.asyncJobOperator = asyncJobOperator;
        this.importPolicyJob = importPolicyJob;
        this.jobExplorer = jobExplorer;
    }

    @PostMapping("/upload-policies")
    public ResponseEntity<Map<String, Object>> uploadCsv(@RequestParam  MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please upload a non-empty CSV file."));
        }

        try {
            String reportId = UUID.randomUUID().toString();
            Path inputFile = tempDir.resolve("input-" + reportId + ".csv");
            Path errorFile = tempDir.resolve("errors-" + reportId + ".csv");

            file.transferTo(inputFile.toFile());

            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", inputFile.toAbsolutePath().toString())
                    .addString("errorFilePath", errorFile.toAbsolutePath().toString())
                    .addString("reportId", reportId)
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = asyncJobOperator.run(importPolicyJob, params);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "File accepted for background processing");
            response.put("jobExecutionId", execution.getId());
            response.put("status", execution.getStatus().toString());
            response.put("statusCheckUrl", "/api/v1/batch/status/" + execution.getId());

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to start batch job: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{executionId}")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable Long executionId) {
        JobExecution execution = jobExplorer.getJobExecution(executionId);

        if (execution == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("jobExecutionId", execution.getId());
        response.put("status", execution.getStatus().toString()); 
        
        if (!execution.getStepExecutions().isEmpty()) {
            StepExecution stepExecution = execution.getStepExecutions().iterator().next();
            int errorCount = stepExecution.getExecutionContext().getInt("errorCount", 0);

            response.put("totalRead", stepExecution.getReadCount());
            response.put("savedCount", stepExecution.getWriteCount());
            response.put("skippedCount", errorCount);

            if (!execution.isRunning() && errorCount > 0) {
                String reportId = execution.getJobParameters().getString("reportId");
                response.put("errorReportDownloadUrl", "/api/v1/batch/errors/" + reportId);
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/errors/{reportId}")
    public ResponseEntity<Resource> downloadErrorReport(@PathVariable String reportId) {
        if (!reportId.matches("^[a-f0-9\\-]{36}$")) {
            return ResponseEntity.badRequest().build();
        }

        Path errorFile = tempDir.resolve("errors-" + reportId + ".csv");

        if (!Files.exists(errorFile)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(errorFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rejected-policies-" + reportId + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
