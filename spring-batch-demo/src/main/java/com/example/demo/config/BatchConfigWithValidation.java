package com.example.demo.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.dto.PolicyDto;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.model.Policy;

import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Configuration
@Profile("valid")
public class BatchConfigWithValidation {
	
	
	private PolicyMapper mapper;
	
	

	public BatchConfigWithValidation(PolicyMapper mapper) {
		super();
		this.mapper = mapper;
	}


	@Bean
    FlatFileItemReader<PolicyDto> policyReader() {
        return new FlatFileItemReaderBuilder<PolicyDto>()
                .name("policyItemReader")
                .resource(new ClassPathResource("policy.csv"))
                .delimited()
                .names("policyNumber", "policyHolder", "premiumAmount", "policyType")
                .targetType(PolicyDto.class)
                .build();
    }
	
	
	    @Bean
	    @StepScope
	    CompositeItemProcessor<PolicyDto, Policy> compositePolicyProcessor(Validator validator, PolicyMapper mapper) {
	        Set<Long> seen = ConcurrentHashMap.newKeySet();

	       
	        ItemProcessor<PolicyDto, PolicyDto> deduplicateFilter = dto -> 
	                seen.add(dto.policyNumber()) ? dto : null;

	                
	        ItemProcessor<PolicyDto, Policy> validateAndMap = dto -> {
	            Set<ConstraintViolation<PolicyDto>> violations = validator.validate(dto);
	            if (!violations.isEmpty()) {
	                throw new IllegalArgumentException("Validation failed: " + violations);
	            }
	            return mapper.toEntity(dto);
	        };

	        return new CompositeItemProcessorBuilder<PolicyDto, Policy>()
	                .delegates(deduplicateFilter, validateAndMap)
	                .build();
	    }

	
	   	@Bean
    JpaItemWriter<Policy> policyWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Policy>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

	
	@Bean
    Step importPolicyStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          FlatFileItemReader<PolicyDto> reader,
                          ItemProcessor<PolicyDto, Policy> processor,
                          JpaItemWriter<Policy> writer) {

        
        return new StepBuilder("importPolicyStep", jobRepository)
                .<PolicyDto, Policy>chunk(3)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
	
	
	@Bean
    Job importPolicyJob(JobRepository jobRepository, Step importPolicyStep) {
        return new JobBuilder("importPolicyJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importPolicyStep)
                .build();
    }
	
	@Bean
    CommandLineRunner runBatchJob(JobOperator jobOperator, Job importPolicyJob) {
        return args -> {
            jobOperator.startNextInstance(importPolicyJob);
        };
    }

	
}
