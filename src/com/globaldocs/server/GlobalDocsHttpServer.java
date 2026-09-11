package com.globaldocs.server;

import com.globaldocs.batch.BatchProcessingEngine;
import com.globaldocs.core.factory.CountryFactoryProvider;
import com.globaldocs.core.factory.DocumentProcessorFactory;
import com.globaldocs.model.BatchSummary;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.model.ProcessingStatus;
import com.globaldocs.util.JsonUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Built-in HTTP REST Server & Static Asset Server for GlobalDocs Solutions.
 * Runs with pure standard library Java - no external heavyweight frameworks needed.
 */
public class GlobalDocsHttpServer {

    private final int port;
    private final String webRoot;
    private final BatchProcessingEngine batchEngine;
    private HttpServer server;

    public GlobalDocsHttpServer(int port, String webRoot) {
        this.port = port;
        this.webRoot = webRoot;
        this.batchEngine = new BatchProcessingEngine();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API Endpoints
        server.createContext("/api/config", new ConfigHandler());
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/documents/process", new ProcessSingleHandler());
        server.createContext("/api/documents/batch", new ProcessBatchHandler(batchEngine));

        // Static Web UI Handler
        server.createContext("/", new StaticFileHandler(webRoot));

        server.setExecutor(null); // default executor
        server.start();
        System.out.println(">>> GlobalDocs HTTP Server running at http://localhost:" + port + "/");
    }

    public void stop() {
        if (server != null) {
            server.stop(1);
        }
        batchEngine.shutdown();
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, byte[] data) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        exchange.sendResponseHeaders(statusCode, data.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    // Handlers
    private static class ConfigHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = JsonUtils.getSystemConfigJson();
            sendResponse(exchange, 200, "application/json; charset=UTF-8", json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Runtime rt = Runtime.getRuntime();
            long freeMem = rt.freeMemory() / (1024 * 1024);
            long totalMem = rt.totalMemory() / (1024 * 1024);
            String json = String.format("{\"status\":\"UP\",\"freeMemoryMb\":%d,\"totalMemoryMb\":%d,\"timestamp\":%d}",
                    freeMem, totalMem, System.currentTimeMillis());
            sendResponse(exchange, 200, "application/json; charset=UTF-8", json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static class ProcessSingleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "application/json", new byte[0]);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}".getBytes());
                return;
            }

            try {
                String body = readRequestBody(exchange);
                DocumentPayload doc = JsonUtils.parseDocumentPayload(body);

                if (doc.getCountry() == null) {
                    sendResponse(exchange, 400, "application/json", "{\"error\":\"Missing country field\"}".getBytes());
                    return;
                }

                // Execute Factory Method Pattern
                DocumentProcessorFactory factory = CountryFactoryProvider.getFactory(doc.getCountry());
                ProcessingResult result = factory.processDocument(doc);

                String json = JsonUtils.toJson(result);
                int code = (result.getStatus() == ProcessingStatus.SUCCESS || result.getStatus() == ProcessingStatus.WARNING) ? 200 : 422;
                sendResponse(exchange, code, "application/json; charset=UTF-8", json.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                String err = String.format("{\"error\":\"%s\"}", ex.getMessage().replace("\"", "\\\""));
                sendResponse(exchange, 500, "application/json; charset=UTF-8", err.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static class ProcessBatchHandler implements HttpHandler {
        private final BatchProcessingEngine engine;

        public ProcessBatchHandler(BatchProcessingEngine engine) {
            this.engine = engine;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "application/json", new byte[0]);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}".getBytes());
                return;
            }

            try {
                String body = readRequestBody(exchange);
                List<DocumentPayload> docs = JsonUtils.parseDocumentPayloadList(body);
                BatchSummary summary = engine.processBatch(docs);
                String json = JsonUtils.toJson(summary);
                sendResponse(exchange, 200, "application/json; charset=UTF-8", json.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                String err = String.format("{\"error\":\"%s\"}", ex.getMessage().replace("\"", "\\\""));
                sendResponse(exchange, 500, "application/json; charset=UTF-8", err.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static class StaticFileHandler implements HttpHandler {
        private final String baseDir;

        public StaticFileHandler(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File file = new File(baseDir, path);
            if (!file.exists() || file.isDirectory()) {
                // Try fallback to index.html for SPA routing
                file = new File(baseDir, "index.html");
            }

            if (!file.exists()) {
                byte[] notFound = "404 Not Found".getBytes(StandardCharsets.UTF_8);
                sendResponse(exchange, 404, "text/plain", notFound);
                return;
            }

            String mime = getMimeType(file.getName());
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] data = fis.readAllBytes();
                sendResponse(exchange, 200, mime, data);
            }
        }

        private String getMimeType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".ico")) return "image/x-icon";
            return "application/octet-stream";
        }
    }
}
