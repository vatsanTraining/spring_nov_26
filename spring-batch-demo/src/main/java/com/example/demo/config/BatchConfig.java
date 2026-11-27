package com.example.demo.config;

import java.net.BindException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.Retryable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.dto.PolicyDto;
import com.example.demo.mapper.PolicyMapper;
import com.example.demo.model.Policy;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.QueryTimeoutException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Configuration
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    @Bean
    @StepScope
    FlatFileItemReader<PolicyDto> policyReader(
            @Value("#{jobParameters['inputFile'] ?: 'policy.csv'}") String inputFile) {
        return new FlatFileItemReaderBuilder<PolicyDto>()
                .name("policyItemReader")
                .resource(new ClassPathResource(inputFile))
                .delimited()
                .names("policyNumber", "policyHolder", "premiumAmount", "policyType")
                .targetType(PolicyDto.class)
                .build();
    }


    @Bean
    @StepScope
    CompositeItemProcessor<PolicyDto, Policy> compositePolicyProcessor(Validator validator, PolicyMapper mapper) {
        Set<Long> seen = ConcurrentHashMap.newKeySet();
        AtomicInteger simulatedAttempts = new AtomicInteger(0);

        ItemProcessor<PolicyDto, PolicyDto> transientFailureSimulator = dto -> {
            if (dto.policyNumber() == 1003L && simulatedAttempts.incrementAndGet() <= 2) {
                log.warn("Simulating transient timeout for policy {} (attempt #{})",
                        dto.policyNumber(), simulatedAttempts.get());
                throw new QueryTimeoutException("Simulated temporary DB/API timeout");
            }
            return dto;
        };

        ItemProcessor<PolicyDto, PolicyDto> deduplicateFilter = dto ->
                seen.add(dto.policyNumber()) ? dto : null;

        ItemProcessor<PolicyDto, Policy> validateAndMap = dto -> {
            Set<ConstraintViolation<PolicyDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("Validation failed for policy " + dto.policyNumber() + ": " + errors);
            }
            return mapper.toEntity(dto);
        };

        return new CompositeItemProcessorBuilder<PolicyDto, Policy>()
                .delegates(transientFailureSimulator, deduplicateFilter, validateAndMap)
                .build();
    }

    @Bean
    JpaItemWriter<Policy> policyWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Policy>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(false) 
                .build();
    }

    @Bean
    RetryPolicy policyRetryPolicy() {
        return RetryPolicy.builder()
                .maxRetries(3)
                .delay(Duration.ofMillis(500))
                .multiplier(2.0) //  2000ms
                .maxDelay(Duration.ofSeconds(5))
                .includes(TransientDataAccessException.class, QueryTimeoutException.class)
                .build();
    }

    @Bean
    RetryListener policyRetryListener() {
        return new RetryListener() {
            @Override
            public void onRetryFailure(RetryPolicy policy, Retryable<?> retryable, Throwable throwable) {
                log.warn("RETRY ATTEMPT FAILED: {} — will retry if attempts remain", throwable.getMessage());
            }

            @Override
            public void onRetryPolicyExhaustion(RetryPolicy policy, Retryable<?> retryable, RetryException exception) {
                log.error("RETRY EXHAUSTED after {} attempts: {} — falling back to skip/fail",
                        exception.getRetryCount(), exception.getMessage());
            }
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
                          RetryPolicy policyRetryPolicy,
                          RetryListener policyRetryListener,
                          SkipListener<PolicyDto, Policy> skipListener) {
        return new StepBuilder("importPolicyStep", jobRepository)
                .<PolicyDto, Policy>chunk(10)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()  // ** note this
                .retryPolicy(policyRetryPolicy)
                .retryListener(policyRetryListener)
                .skipLimit(10)
                .skip(FlatFileParseException.class)
                .skip(BindException.class)
                .skip(IllegalArgumentException.class)
                .skip(DataIntegrityViolationException.class)
                .skip(EntityExistsException.class)
                .skip(TransientDataAccessException.class)
                .skip(QueryTimeoutException.class)
                .listener(skipListener)
                .build();
    }

    @Bean
    Job importPolicyJob(JobRepository jobRepository, Step importPolicyStep) {
        return new JobBuilder("importPolicyJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importPolicyStep)
                .build();
    }
}


