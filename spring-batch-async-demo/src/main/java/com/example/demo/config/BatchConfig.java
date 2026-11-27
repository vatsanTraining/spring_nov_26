package com.example.demo.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;


import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.BindException;

import com.example.demo.ifaces.PolicyRepoistory;
import com.example.demo.listeners.PolicySkipListener;
import com.example.demo.model.Policy;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.QueryTimeoutException;

@Configuration
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    private final PolicySkipListener policySkipListener;

    public BatchConfig(PolicySkipListener policySkipListener) {
        this.policySkipListener = policySkipListener;
    }

    @Bean(name = "batchTaskExecutor")
     TaskExecutor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("policy-batch-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "asyncJobOperator")
     JobOperatorFactoryBean asyncJobOperator(
            JobRepository jobRepository,
            @Qualifier("batchTaskExecutor") TaskExecutor batchTaskExecutor) {

        JobOperatorFactoryBean factoryBean = new JobOperatorFactoryBean();
        factoryBean.setJobRepository(jobRepository);
        factoryBean.setTaskExecutor(batchTaskExecutor);
        return factoryBean;
    }

    @Bean
    @StepScope
     FlatFileItemReader<Policy> policyReader(
            @Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<Policy>()
                .name("policyItemReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .names("policyNumber", "policyHolder", "premiumAmount", "policyType")
                .targetType(Policy.class)
                .build();
    }

    @Bean
    @StepScope
     ItemProcessor<Policy, Policy> policyProcessor(PolicyRepoistory repo) {
        Set<Long> seenInCurrentFile = new HashSet<>();

        return policy -> {
            if (policy.getPolicyHolder() == null ||
                policy.getPolicyHolder().length() < 3 ||
                policy.getPolicyHolder().length() > 12) {
                throw new IllegalArgumentException("Policy Holder Name must be between 3 and 12 characters");
            }

            long id = policy.getPolicyNumber();
            if (seenInCurrentFile.contains(id)) {
                throw new IllegalArgumentException("Duplicate policyNumber within the uploaded CSV file");
            }
            if (repo.existsById(id)) {
                throw new IllegalArgumentException("policyNumber already exists in database");
            }

            seenInCurrentFile.add(id);
            return policy;
        };
    }

    @Bean
     JpaItemWriter<Policy> policyWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Policy>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
     Step importPolicyStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Policy> reader,
            ItemProcessor<Policy, Policy> processor,
            JpaItemWriter<Policy> writer) {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .includes(TransientDataAccessException.class, QueryTimeoutException.class)
                .delay(Duration.ofSeconds(1))      
                .multiplier(2.0)                   
                .maxDelay(Duration.ofSeconds(10))  
                .build();

        return new StepBuilder("importPolicyStep", jobRepository)
                .<Policy, Policy>chunk(10)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryPolicy(retryPolicy)
                .skipLimit(5)
                .skip(FlatFileParseException.class)
                .skip(BindException.class)
                .skip(IllegalArgumentException.class)
                .listener(policySkipListener)
                .build();
    }

    @Bean
     JobExecutionListener tempFileCleanupListener() {
        return new JobExecutionListener() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                String inputFilePath = jobExecution.getJobParameters().getString("filePath");
                if (inputFilePath != null) {
                    try {
                        Files.deleteIfExists(Paths.get(inputFilePath));
                    } catch (Exception e) {
                        log.warn("Could not delete temp input file {}: {}", inputFilePath, e.getMessage());
                    }
                }
            }
        };
    }

    @Bean
     Job importPolicyJob(
            JobRepository jobRepository,
            Step importPolicyStep,
            JobExecutionListener tempFileCleanupListener) {
        return new JobBuilder("importPolicyJob", jobRepository)
                .start(importPolicyStep)
                .listener(tempFileCleanupListener)
                .build();
    }
}
