package com.example.demo.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    Tasklet helloWorldTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("=========================================");
            System.out.println("  Hello World from Spring Boot 4.0!      ");
            System.out.println("=========================================");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
     Step helloWorldStep(JobRepository jobRepository, 
                              PlatformTransactionManager transactionManager, 
                              Tasklet helloWorldTasklet) {
        return new StepBuilder("helloWorldStep", jobRepository)
                .tasklet(helloWorldTasklet, transactionManager)
                .build();
    }

    @Bean
     Job helloWorldJob(JobRepository jobRepository, Step helloWorldStep) {
        return new JobBuilder("helloWorldJob", jobRepository)
                .start(helloWorldStep)
                .build();
    }
}
