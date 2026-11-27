package org.demo.stepbystep.config;

import java.net.BindException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.dto.PolicyDto;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.model.Policy;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Configuration
@Profile("tolerent")
public class BatchConfigWithFaultTolerent {
	
	
		
    private static final Logger log = LoggerFactory.getLogger(BatchConfigWithFaultTolerent.class);


	


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
                .usePersist(true)
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

	
	@Bean
    SkipListener<PolicyDto, Policy> policySkipListener() {
        return new SkipListener<>() {
            @Override
            public void onSkipInRead(Throwable t) {
                if (t instanceof FlatFileParseException parseEx) {
                    log.warn("SKIPPED IN READ [Line {}]: {} | Raw input: '{}'",
                            parseEx.getLineNumber(), parseEx.getMessage(), parseEx.getInput());
                } else {
                    log.warn("SKIPPED IN READ: {}", t.getMessage());
                }
            }

            @Override
            public void onSkipInProcess(PolicyDto item, Throwable t) {
                log.warn("SKIPPED IN PROCESS [Policy {}]: {}", item.policyNumber(), t.getMessage());
            }

            @Override
            public void onSkipInWrite(Policy item, Throwable t) {
                log.warn("SKIPPED IN WRITE [Policy {}]: {}", item.getPolicyNumber(), t.getMessage());
            }
        };
    }

    @Bean
    Step importPolicyStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          FlatFileItemReader<PolicyDto> reader,
                          CompositeItemProcessor<PolicyDto, Policy> processor,
                          JpaItemWriter<Policy> writer,
                          SkipListener<PolicyDto, Policy> skipListener) {

        return new StepBuilder("importPolicyStep", jobRepository)
                .<PolicyDto, Policy>chunk(10)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .skipLimit(10) 
                 .skip(FlatFileParseException.class)         
                 .skip(BindException.class)                   
                .skip(IllegalArgumentException.class)        
                                .skip(DataIntegrityViolationException.class) 
                                                .skip(EntityExistsException.class)          
                                                
                .listener(skipListener)
                .build();
    }
	
}
