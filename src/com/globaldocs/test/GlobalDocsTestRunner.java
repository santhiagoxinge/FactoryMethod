package com.globaldocs.test;

import com.globaldocs.batch.BatchProcessingEngine;
import com.globaldocs.core.factory.CountryFactoryProvider;
import com.globaldocs.core.factory.DocumentProcessorFactory;
import com.globaldocs.model.BatchSummary;
import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentFormat;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.model.ProcessingStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Self-contained Automated Test Runner validating all workshop requirements:
 * 1. Factory Method pattern integration
 * 2. Country-specific regulatory validation (Colombia, Mexico, Argentina, Chile)
 * 3. 5 Document types & 7 Formats
 * 4. Batch processing engine
 * 5. Error handling & containment
 */
public class GlobalDocsTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println(" RUNNING AUTOMATED FACTORY METHOD TEST SUITE");
        System.out.println("==========================================================");

        testFactoryProviderResolutions();
        testColombianInvoiceProcessing();
        testMexicanContractProcessing();
        testArgentineReportProcessing();
        testChileanCertificateProcessing();
        testFormatRejection();
        testRegulatoryTaxIdValidation();
        testBatchProcessingEngine();

        System.out.println("==========================================================");
        System.out.printf(" TEST RESULTS: %d PASSED | %d FAILED\n", passed, failed);
        System.out.println("==========================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testFactoryProviderResolutions() {
        assertTest("FactoryProvider: Resolves Colombia Factory", () -> {
            DocumentProcessorFactory f = CountryFactoryProvider.getFactory(Country.COLOMBIA);
            return f != null && f.getCountry() == Country.COLOMBIA;
        });

        assertTest("FactoryProvider: Resolves Mexico Factory", () -> {
            DocumentProcessorFactory f = CountryFactoryProvider.getFactory(Country.MEXICO);
            return f != null && f.getCountry() == Country.MEXICO;
        });

        assertTest("FactoryProvider: Resolves Argentina Factory", () -> {
            DocumentProcessorFactory f = CountryFactoryProvider.getFactory(Country.ARGENTINA);
            return f != null && f.getCountry() == Country.ARGENTINA;
        });

        assertTest("FactoryProvider: Resolves Chile Factory", () -> {
            DocumentProcessorFactory f = CountryFactoryProvider.getFactory(Country.CHILE);
            return f != null && f.getCountry() == Country.CHILE;
        });
    }

    private static void testColombianInvoiceProcessing() {
        assertTest("Colombia Invoice: Generates valid DIAN CUFE stamp", () -> {
            DocumentPayload doc = DocumentPayload.builder()
                    .title("Factura Venta Test")
                    .country(Country.COLOMBIA)
                    .documentType(DocumentType.ELECTRONIC_INVOICE)
                    .format(DocumentFormat.PDF)
                    .taxIdentifier("900123456-1")
                    .amount(5000000.0)
                    .build();

            DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(Country.COLOMBIA);
            ProcessingResult res = factory.processDocument(doc);
            return res.getStatus() == ProcessingStatus.SUCCESS &&
                    res.getRegulatoryStamp() != null &&
                    res.getRegulatoryStamp().startsWith("CUFE-");
        });
    }

    private static void testMexicanContractProcessing() {
        assertTest("Mexico Contract: Enforces SAT / NOM-151 compliance", () -> {
            DocumentPayload doc = DocumentPayload.builder()
                    .title("Contrato Mercantil Test")
                    .country(Country.MEXICO)
                    .documentType(DocumentType.LEGAL_CONTRACT)
                    .format(DocumentFormat.DOCX)
                    .taxIdentifier("GDM180425ABC")
                    .amount(50000.0)
                    .content("Contrato marco para la distribución de software en México.")
                    .build();

            DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(Country.MEXICO);
            ProcessingResult res = factory.processDocument(doc);
            return res.getStatus() == ProcessingStatus.SUCCESS &&
                    res.getRegulatoryStamp().startsWith("UUID-SAT-");
        });
    }

    private static void testArgentineReportProcessing() {
        assertTest("Argentina Report: Enforces AFIP CAE compliance", () -> {
            DocumentPayload doc = DocumentPayload.builder()
                    .title("Balance Anual Test")
                    .country(Country.ARGENTINA)
                    .documentType(DocumentType.FINANCIAL_REPORT)
                    .format(DocumentFormat.XLSX)
                    .taxIdentifier("30-12345678-9")
                    .amount(1200000.0)
                    .build();

            DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(Country.ARGENTINA);
            ProcessingResult res = factory.processDocument(doc);
            return res.getStatus() == ProcessingStatus.SUCCESS &&
                    res.getRegulatoryStamp().startsWith("CAE-");
        });
    }

    private static void testChileanCertificateProcessing() {
        assertTest("Chile Certificate: Enforces SII DTE timestamping", () -> {
            DocumentPayload doc = DocumentPayload.builder()
                    .title("Certificado Digital SII Test")
                    .country(Country.CHILE)
                    .documentType(DocumentType.DIGITAL_CERTIFICATE)
                    .format(DocumentFormat.PDF)
                    .taxIdentifier("12345678-K")
                    .amount(0.0)
                    .build();

            DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(Country.CHILE);
            ProcessingResult res = factory.processDocument(doc);
            return res.getStatus() == ProcessingStatus.SUCCESS &&
                    res.getRegulatoryStamp().startsWith("SII-DTE-FOLIO-");
        });
    }

    private static void testFormatRejection() {
        assertTest("Error Handling: Rejects illegal format .txt for Colombian Electronic Invoice", () -> {
            DocumentPayload doc = DocumentPayload.builder()
                    .title("Invoice with Illegal TXT format")
                    .country(Country.COLOMBIA)
                    .documentType(DocumentType.ELECTRONIC_INVOICE)
                    .format(DocumentFormat.TXT)
                    .taxIdentifier("900123456-1")
                    .amount(100.0)
                    .build();

            DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(Country.COLOMBIA);
            ProcessingResult res = factory.processDocument(doc);
            return res.getStatus() == ProcessingStatus.FAILED && !res.getErrors().isEmpty();
        });
    }

    private static void testRegulatoryTaxIdValidation() {
        assertTest("Error Handling: Rejects invalid Mexican RFC format", () -> {
            DocumentPayload doc = DocumentPayload.builder()
                    .title("Invalid RFC Invoice")
                    .country(Country.MEXICO)
                    .documentType(DocumentType.ELECTRONIC_INVOICE)
                    .format(DocumentFormat.PDF)
                    .taxIdentifier("INVALID_RFC_123")
                    .amount(100.0)
                    .build();

            DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(Country.MEXICO);
            ProcessingResult res = factory.processDocument(doc);
            return res.getStatus() == ProcessingStatus.REJECTED;
        });
    }

    private static void testBatchProcessingEngine() {
        assertTest("Batch Engine: Processes concurrent list with mixed valid and invalid payloads", () -> {
            List<DocumentPayload> batch = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                batch.add(DocumentPayload.builder()
                        .title("Valid Doc " + i)
                        .country(Country.COLOMBIA)
                        .documentType(DocumentType.ELECTRONIC_INVOICE)
                        .format(DocumentFormat.PDF)
                        .taxIdentifier("900123456-1")
                        .amount(1000.0 * (i + 1))
                        .build());
            }
            // Add 2 intentionally failing documents
            batch.add(DocumentPayload.builder()
                    .title("Invalid Country Doc")
                    .country(Country.CHILE)
                    .documentType(DocumentType.TAX_DECLARATION)
                    .format(DocumentFormat.PDF)
                    .taxIdentifier("INVALID-RUT")
                    .build());

            BatchProcessingEngine engine = new BatchProcessingEngine(4);
            BatchSummary summary = engine.processBatch(batch);
            engine.shutdown();

            return summary.getTotalDocuments() == 11 &&
                    summary.getSuccessCount() == 10 &&
                    summary.getRejectedCount() == 1;
        });
    }

    private static void assertTest(String description, TestCase test) {
        try {
            if (test.execute()) {
                System.out.println("  [PASS] " + description);
                passed++;
            } else {
                System.err.println("  [FAIL] " + description);
                failed++;
            }
        } catch (Exception e) {
            System.err.println("  [FAIL] " + description + " -> Exception: " + e.getMessage());
            failed++;
        }
    }

    @FunctionalInterface
    interface TestCase {
        boolean execute() throws Exception;
    }
}
