package com.globaldocs;

import com.globaldocs.batch.BatchProcessingEngine;
import com.globaldocs.core.factory.CountryFactoryProvider;
import com.globaldocs.core.factory.DocumentProcessorFactory;
import com.globaldocs.model.BatchSummary;
import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentFormat;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.server.GlobalDocsHttpServer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Application Runner for GlobalDocs Solutions.
 * Supports both standalone CLI demonstration mode and embedded HTTP Web Server mode.
 */
public class GlobalDocsApplication {

    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        boolean startServer = true;
        int port = DEFAULT_PORT;

        for (int i = 0; i < args.length; i++) {
            if ("--cli".equalsIgnoreCase(args[i])) {
                startServer = false;
            } else if ("--port".equalsIgnoreCase(args[i]) && i + 1 < args.length) {
                port = Integer.parseInt(args[++i]);
            }
        }

        printBanner();

        if (startServer) {
            startWebServer(port);
        } else {
            runCliDemonstration();
        }
    }

    public static void startWebServer(int port) {
        try {
            // Locate web resources directory
            String webDir = findWebDirectory();
            System.out.println("Serving web assets from: " + new File(webDir).getAbsolutePath());

            GlobalDocsHttpServer server = new GlobalDocsHttpServer(port, webDir);
            server.start();

            System.out.println("==================================================================");
            System.out.println("  GlobalDocs Solutions Web Dashboard is LIVE!");
            System.out.println("  Access URL: http://localhost:" + port + "/");
            System.out.println("  Press Ctrl+C to terminate the server.");
            System.out.println("==================================================================");

            // Keep main thread alive
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runCliDemonstration() {
        System.out.println("\n--- RUNNING GLOBALDOCS FACTORY METHOD DEMONSTRATION ---\n");

        // 1. Ingest Electronic Invoice for Colombia (DIAN)
        DocumentPayload colombianInvoice = DocumentPayload.builder()
                .title("Factura Venta Servicios Tecnológicos 2026")
                .country(Country.COLOMBIA)
                .documentType(DocumentType.ELECTRONIC_INVOICE)
                .format(DocumentFormat.PDF)
                .taxIdentifier("900123456-1")
                .amount(25000000.0)
                .content("Factura de venta electrónica por consultoría cloud a cliente corporativo.")
                .build();

        executeSingleDocDemo(colombianInvoice);

        // 2. Ingest Legal Contract for Mexico (SAT / NOM-151)
        DocumentPayload mexicanContract = DocumentPayload.builder()
                .title("Contrato Marco de Prestación de Servicios Mercantiles")
                .country(Country.MEXICO)
                .documentType(DocumentType.LEGAL_CONTRACT)
                .format(DocumentFormat.DOCX)
                .taxIdentifier("GDM180425ABC")
                .amount(120000.0)
                .content("Contrato mercantil celebrado conforme a las leyes de la República Mexicana.")
                .build();

        executeSingleDocDemo(mexicanContract);

        // 3. Ingest Financial Report for Argentina (AFIP / FACPCE)
        DocumentPayload argentineReport = DocumentPayload.builder()
                .title("Balance General Ejercicio Fiscal 2025")
                .country(Country.ARGENTINA)
                .documentType(DocumentType.FINANCIAL_REPORT)
                .format(DocumentFormat.XLSX)
                .taxIdentifier("30-12345678-9")
                .amount(4500000.0)
                .content("Estado de situación patrimonial auditado conforme a RT 54 de la FACPCE.")
                .build();

        executeSingleDocDemo(argentineReport);

        // 4. Ingest Digital Certificate for Chile (SII / PKI)
        DocumentPayload chileCertificate = DocumentPayload.builder()
                .title("Certificado Digital de Firma Electrónica Avanzada")
                .country(Country.CHILE)
                .documentType(DocumentType.DIGITAL_CERTIFICATE)
                .format(DocumentFormat.PDF)
                .taxIdentifier("12345678-K")
                .amount(0.0)
                .content("Certificado digital emitido por Entidad Acreditadora para timbraje de DTE.")
                .build();

        executeSingleDocDemo(chileCertificate);

        // 5. Ingest Tax Declaration for Colombia (DIAN)
        DocumentPayload colombiaTax = DocumentPayload.builder()
                .title("Declaración de Renta Persona Jurídica 2025")
                .country(Country.COLOMBIA)
                .documentType(DocumentType.TAX_DECLARATION)
                .format(DocumentFormat.PDF)
                .taxIdentifier("900123456-1")
                .amount(78000000.0)
                .content("Declaración y pago del impuesto sobre la renta y complementarios.")
                .build();

        executeSingleDocDemo(colombiaTax);

        // 6. Demonstrate Batch Processing (Concurrency + Error Containment)
        System.out.println("\n--- RUNNING BATCH PROCESSING SIMULATION (50,000+ DAILY REQ) ---");
        List<DocumentPayload> batch = new ArrayList<>();
        batch.add(colombianInvoice);
        batch.add(mexicanContract);
        batch.add(argentineReport);
        batch.add(chileCertificate);
        batch.add(colombiaTax);

        // Intentionally inject invalid format to test error containment
        batch.add(DocumentPayload.builder()
                .title("Corrupted Invoice with Invalid Format")
                .country(Country.COLOMBIA)
                .documentType(DocumentType.ELECTRONIC_INVOICE)
                .format(DocumentFormat.TXT) // Illegal format for DIAN invoice
                .taxIdentifier("900123456-1")
                .amount(1000.0)
                .build());

        BatchProcessingEngine batchEngine = new BatchProcessingEngine();
        BatchSummary summary = batchEngine.processBatch(batch);
        batchEngine.shutdown();

        System.out.println(String.format("Batch ID: %s | Total: %d | Success: %d | Failed/Rejected: %d | Speed: %.1f docs/sec in %d ms",
                summary.getBatchId(), summary.getTotalDocuments(), summary.getSuccessCount(),
                summary.getFailedCount() + summary.getRejectedCount(),
                summary.getThroughputDocsPerSecond(), summary.getTotalExecutionTimeMs()));
    }

    private static void executeSingleDocDemo(DocumentPayload doc) {
        try {
            System.out.println("------------------------------------------------------------------");
            System.out.println(String.format(">> Ingesting [%s] - %s | Country: %s | Format: .%s",
                    doc.getDocumentType().getEnglishName(), doc.getTitle(), doc.getCountry(), doc.getFormat().getExtension()));

            // Resolve Factory Method Creator
            DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(doc.getCountry());
            ProcessingResult result = factory.processDocument(doc);

            System.out.println("   [FACTORY USED]: " + result.getFactoryUsed());
            System.out.println("   [PROCESSOR]   : " + result.getProcessorUsed());
            System.out.println("   [STATUS]      : " + result.getStatus());
            System.out.println("   [REG STAMP]   : " + result.getRegulatoryStamp());
            System.out.println("   [AUDIT LOGS]  : " + result.getAuditLogs().size() + " checkpoints passed");
        } catch (Exception e) {
            System.err.println("   [FAILED]      : " + e.getMessage());
        }
    }

    private static String findWebDirectory() {
        String[] candidates = {
                "src/main/resources/web",
                "web",
                "../web",
                "src/web"
        };
        for (String c : candidates) {
            File f = new File(c);
            if (f.exists() && f.isDirectory()) {
                return c;
            }
        }
        return "web";
    }

    private static void printBanner() {
        System.out.println("==================================================================");
        System.out.println("   GLOBALDOCS SOLUTIONS - ENTERPRISE DOCUMENT PROCESSING SYSTEM   ");
        System.out.println("   Factory Method Design Pattern | Java 25 LTS Standard Core     ");
        System.out.println("   Multinational: Colombia (DIAN) | Mexico (SAT)                 ");
        System.out.println("                  Argentina (AFIP) | Chile (SII)                 ");
        System.out.println("==================================================================");
    }
}
