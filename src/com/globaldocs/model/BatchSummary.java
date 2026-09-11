package com.globaldocs.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Summary outcome of a batch execution run (50,000+ daily volume requirement simulation).
 */
public class BatchSummary {
    private final String batchId;
    private final int totalDocuments;
    private final int successCount;
    private final int warningCount;
    private final int rejectedCount;
    private final int failedCount;
    private final long totalExecutionTimeMs;
    private final double throughputDocsPerSecond;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final List<ProcessingResult> results;

    public BatchSummary(String batchId, List<ProcessingResult> results, long totalExecutionTimeMs, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.batchId = batchId;
        this.results = results != null ? new ArrayList<>(results) : new ArrayList<>();
        this.totalDocuments = this.results.size();
        this.totalExecutionTimeMs = totalExecutionTimeMs;
        this.startedAt = startedAt;
        this.completedAt = completedAt;

        int s = 0, w = 0, r = 0, f = 0;
        for (ProcessingResult res : this.results) {
            if (res.getStatus() == ProcessingStatus.SUCCESS) s++;
            else if (res.getStatus() == ProcessingStatus.WARNING) w++;
            else if (res.getStatus() == ProcessingStatus.REJECTED) r++;
            else if (res.getStatus() == ProcessingStatus.FAILED) f++;
        }
        this.successCount = s;
        this.warningCount = w;
        this.rejectedCount = r;
        this.failedCount = f;

        double seconds = totalExecutionTimeMs > 0 ? totalExecutionTimeMs / 1000.0 : 0.001;
        this.throughputDocsPerSecond = Math.round((this.totalDocuments / seconds) * 100.0) / 100.0;
    }

    public String getBatchId() {
        return batchId;
    }

    public int getTotalDocuments() {
        return totalDocuments;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public int getRejectedCount() {
        return rejectedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public long getTotalExecutionTimeMs() {
        return totalExecutionTimeMs;
    }

    public double getThroughputDocsPerSecond() {
        return throughputDocsPerSecond;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public List<ProcessingResult> getResults() {
        return Collections.unmodifiableList(results);
    }
}
