
package com.example.demo.config;

import java.time.LocalDateTime;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobExecutionConfig {

    @Bean
     ApplicationRunner runAllJobs(
            JobOperator jobOperator,
            @Qualifier("helloWorldJob") Job helloWorldJob,
            @Qualifier("chunkJob") Job chunkJob) {

        return args -> {

            System.out.println("Starting Spring Batch Jobs...");

            JobParameters taskletParams =
                    new JobParametersBuilder()
                            .addLocalDateTime(
                                    "run.timestamp", LocalDateTime.now())
                            .toJobParameters();

            JobExecution taskletExecution =
                    jobOperator.start(helloWorldJob, taskletParams);

            System.out.println(
                    "Hello World Job Status: "
                    + taskletExecution.getStatus()
            );

            JobParameters chunkParams =
                    new JobParametersBuilder()
                            .addLocalDateTime(
                                    "run.timestamp", LocalDateTime.now())
                            .toJobParameters();

            JobExecution chunkExecution =
                    jobOperator.start(chunkJob, chunkParams);

            System.out.println(
                    "Chunk Job Status: "
                    + chunkExecution.getStatus()
            );

            System.out.println("Job launch requests submitted.");
        };
    }
}
