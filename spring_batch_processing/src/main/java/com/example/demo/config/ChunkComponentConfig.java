
package com.example.demo.config;

import com.example.demo.model.TransactionRecord;

import java.util.List;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ChunkComponentConfig {

    @Bean
    ListItemReader<TransactionRecord> transactionReader() {

        return new ListItemReader<>(List.of(
            new TransactionRecord("TXN001", "ACC123", 1500.50, "PENDING"),
            new TransactionRecord("TXN002", "ACC456", 2500.00, "PENDING"),
            new TransactionRecord("TXN003", "ACC789", 500.25, "PENDING")
        ));
    }

    @Bean
     ItemProcessor<TransactionRecord, TransactionRecord>
            transactionProcessor() {

        return item -> {
            item.setStatus("PROCESSED");

            System.out.println(
                "Processing item: " + item.getTransactionId()
            );

            return item;
        };
    }

    @Bean
     ItemWriter<TransactionRecord> transactionWriter() {

        return chunk -> {

            System.out.println(
                "--- Writing Chunk of Size: " + chunk.size() + " ---"
            );

            for (TransactionRecord item : chunk) {
                System.out.println("Saved to target system: " + item);
            }
        };
    }


@Bean
 Step step(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ListItemReader<TransactionRecord> reader,
        ItemProcessor<TransactionRecord, TransactionRecord> processor,
        ItemWriter<TransactionRecord> writer) {

    return new StepBuilder("step", jobRepository)

            .<TransactionRecord, TransactionRecord>chunk(10)

            .transactionManager(transactionManager)

            .reader(reader)
            .processor(processor)
            .writer(writer)

            .build();
}

    @Bean
     Job chunkJob(JobRepository jobRepository, Step step) {

        return new JobBuilder("chunkjob", jobRepository)
                .start(step)
                .build();
    }
}
