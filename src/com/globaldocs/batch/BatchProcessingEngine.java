package com.globaldocs.batch;

import com.globaldocs.core.factory.CountryFactoryProvider;
import com.globaldocs.core.factory.DocumentProcessorFactory;
import com.globaldocs.model.BatchSummary;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.model.ProcessingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Batch processing engine capable of processing thousands of documents concurrently.
 * Designed to satisfy GlobalDocs requirement: 50,000+ daily documents with error containment.
 */
public class BatchProcessingEngine {

    private final ExecutorService threadPool;

    public BatchProcessingEngine() {
        // Pool size matching available processor cores for high throughput
        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        this.threadPool = Executors.newFixedThreadPool(threads);
    }

    public BatchProcessingEngine(int threadPoolSize) {
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Processes a batch of documents concurrently with error containment.
     * Even if individual documents fail or face regulatory rejection, the batch completes.
     *
     * @param documents List of incoming document payloads
     * @return BatchSummary containing all results, timings, and status breakdowns
     */
    public BatchSummary processBatch(List<DocumentPayload> documents) {
        String batchId = "BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime startedAt = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        if (documents == null || documents.isEmpty()) {
            return new BatchSummary(batchId, Collections.emptyList(), 0, startedAt, LocalDateTime.now());
        }

        List<Callable<ProcessingResult>> tasks = new ArrayList<>(documents.size());
        for (DocumentPayload doc : documents) {
            tasks.add(() -> processSingleDocument(doc));
        }

        List<ProcessingResult> results = new ArrayList<>(documents.size());
        try {
            List<Future<ProcessingResult>> futures = threadPool.invokeAll(tasks);
            for (Future<ProcessingResult> future : futures) {
                results.add(future.get());
            }
        } catch (Exception e) {
            // Thread interruption fallback
            System.err.println("Batch execution interrupted: " + e.getMessage());
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        LocalDateTime completedAt = LocalDateTime.now();

        return new BatchSummary(batchId, results, totalDuration, startedAt, completedAt);
    }

    /**
     * Executes single document pipeline using the Factory Method pattern.
     */
    public ProcessingResult processSingleDocument(DocumentPayload doc) {
        try {
            if (doc.getCountry() == null) {
                throw new IllegalArgumentException("Target country must be specified");
            }
            // 1. Resolve Creator for country via registry
            DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(doc.getCountry());
            // 2. Creator executes Template Method, invoking Factory Method internally
            return factory.processDocument(doc);
        } catch (Exception ex) {
            // Error containment: ensure batch does not crash on corrupted payload
            return ProcessingResult.builder()
                    .documentId(doc.getDocumentId())
                    .documentTitle(doc.getTitle() != null ? doc.getTitle() : "Corrupted Document")
                    .country(doc.getCountry())
                    .documentType(doc.getDocumentType())
                    .format(doc.getFormat())
                    .status(ProcessingStatus.FAILED)
                    .regulatoryStamp("ERROR")
                    .authorityValidationMessage("Batch Ingestion Error: " + ex.getMessage())
                    .factoryUsed("N/A")
                    .processorUsed("N/A")
                    .processingTimeMs(0)
                    .addError(ex.getMessage())
                    .build();
        }
    }

    public void shutdown() {
        threadPool.shutdown();
    }
}
