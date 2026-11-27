package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.demo.ifaces.*;

@SpringBatchTest
@SpringBootTest
class BatchConfigTest {

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private PolicyRepository policyRepository;

    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
    }

    @Test
    void testImportPolicyStep_handlesRetriesSkipsAndDeduplication() {
        JobExecution jobExecution = jobOperatorTestUtils.startStep("importPolicyStep");
        
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertTrue(stepExecution.getWriteCount() > 0);
        assertEquals(1, stepExecution.getFilterCount()); 
        assertEquals(1, stepExecution.getSkipCount()); 
        assertTrue(policyRepository.existsById(1003L));
    }
}
