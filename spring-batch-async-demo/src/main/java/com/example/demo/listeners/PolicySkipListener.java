package com.example.demo.listeners;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.model.Policy;

@Component
@StepScope
public class PolicySkipListener implements SkipListener<Policy, Policy>, StepExecutionListener {

    private BufferedWriter errorWriter;
    private final String errorFilePath;
    private int errorCount = 0;

    public PolicySkipListener(
            @Value("#{jobParameters['errorFilePath']}") String errorFilePath) {
        this.errorFilePath = errorFilePath;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        try {
            Path path = Paths.get(errorFilePath);
            errorWriter = new BufferedWriter(new FileWriter(path.toFile()));
            errorWriter.write("Row/Input,Stage,Reason");
            errorWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize error report file", e);
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException parseEx) {
            writeError("Line " + parseEx.getLineNumber() + ": " + parseEx.getInput(),
                       "READ",
                       parseEx.getCause() != null ? parseEx.getCause().getMessage() : parseEx.getMessage());
        } else {
            writeError("Unknown line", "READ", t.getMessage());
        }
    }

    @Override
    public void onSkipInProcess(Policy item, Throwable t) {
        writeError(item != null ? item.toString() : "null", "PROCESS", t.getMessage());
    }

    @Override
    public void onSkipInWrite(Policy item, Throwable t) {
        writeError(item != null ? item.toString() : "null", "WRITE", t.getMessage());
    }

    private void writeError(String rawData, String stage, String reason) {
        try {
            errorCount++;
            String safeData = "\"" + (rawData != null ? rawData.replace("\"", "'") : "null") + "\"";
            String safeReason = "\"" + (reason != null ? reason.replace("\"", "'") : "Unknown error") + "\"";
            errorWriter.write(safeData + "," + stage + "," + safeReason);
            errorWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to error report", e);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        try {
            if (errorWriter != null) {
                errorWriter.flush();
                errorWriter.close();
            }
            stepExecution.getExecutionContext().putInt("errorCount", errorCount);

            // Delete empty error report if 0 errors occurred
            if (errorCount == 0 && errorFilePath != null) {
                Files.deleteIfExists(Paths.get(errorFilePath));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed during post-step file cleanup", e);
        }
        return stepExecution.getExitStatus();
    }
}
