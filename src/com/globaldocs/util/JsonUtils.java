package com.globaldocs.util;

import com.globaldocs.model.BatchSummary;
import com.globaldocs.model.Country;
import com.globaldocs.model.DocumentFormat;
import com.globaldocs.model.DocumentPayload;
import com.globaldocs.model.DocumentType;
import com.globaldocs.model.ProcessingResult;
import com.globaldocs.model.ProcessingStatus;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lightweight, zero-dependency JSON parser and serializer for GlobalDocs API.
 */
public class JsonUtils {

    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String toJson(ProcessingResult res) {
        StringBuilder sb = new StringBuilder("{");
        appendString(sb, "documentId", res.getDocumentId(), true);
        appendString(sb, "documentTitle", res.getDocumentTitle(), true);
        appendString(sb, "country", res.getCountry() != null ? res.getCountry().name() : null, true);
        appendString(sb, "countryDisplayName", res.getCountry() != null ? res.getCountry().getDisplayName() : null, true);
        appendString(sb, "taxAuthority", res.getCountry() != null ? res.getCountry().getTaxAuthority() : null, true);
        appendString(sb, "documentType", res.getDocumentType() != null ? res.getDocumentType().name() : null, true);
        appendString(sb, "documentTypeDisplay", res.getDocumentType() != null ? res.getDocumentType().getEnglishName() : null, true);
        appendString(sb, "format", res.getFormat() != null ? res.getFormat().getExtension() : null, true);
        appendString(sb, "status", res.getStatus() != null ? res.getStatus().name() : null, true);
        appendString(sb, "regulatoryStamp", res.getRegulatoryStamp(), true);
        appendString(sb, "authorityValidationMessage", res.getAuthorityValidationMessage(), true);
        appendString(sb, "factoryUsed", res.getFactoryUsed(), true);
        appendString(sb, "processorUsed", res.getProcessorUsed(), true);
        appendNumber(sb, "processingTimeMs", res.getProcessingTimeMs(), true);
        appendString(sb, "completedAt", res.getCompletedAt() != null ? res.getCompletedAt().format(ISO_FMT) : null, true);

        // Arrays
        appendStringList(sb, "auditLogs", res.getAuditLogs(), true);
        appendStringList(sb, "warnings", res.getWarnings(), true);
        appendStringList(sb, "errors", res.getErrors(), false);

        sb.append("}");
        return sb.toString();
    }

    public static String toJson(BatchSummary summary) {
        StringBuilder sb = new StringBuilder("{");
        appendString(sb, "batchId", summary.getBatchId(), true);
        appendNumber(sb, "totalDocuments", summary.getTotalDocuments(), true);
        appendNumber(sb, "successCount", summary.getSuccessCount(), true);
        appendNumber(sb, "warningCount", summary.getWarningCount(), true);
        appendNumber(sb, "rejectedCount", summary.getRejectedCount(), true);
        appendNumber(sb, "failedCount", summary.getFailedCount(), true);
        appendNumber(sb, "totalExecutionTimeMs", summary.getTotalExecutionTimeMs(), true);
        appendNumber(sb, "throughputDocsPerSecond", summary.getThroughputDocsPerSecond(), true);
        appendString(sb, "startedAt", summary.getStartedAt() != null ? summary.getStartedAt().format(ISO_FMT) : null, true);
        appendString(sb, "completedAt", summary.getCompletedAt() != null ? summary.getCompletedAt().format(ISO_FMT) : null, true);

        sb.append("\"results\":[");
        List<ProcessingResult> results = summary.getResults();
        for (int i = 0; i < results.size(); i++) {
            sb.append(toJson(results.get(i)));
            if (i < results.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String getSystemConfigJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"countries\":[");
        Country[] countries = Country.values();
        for (int i = 0; i < countries.length; i++) {
            Country c = countries[i];
            sb.append(String.format("{\"code\":\"%s\",\"name\":\"%s\",\"authority\":\"%s\",\"currency\":\"%s\",\"fullName\":\"%s\"}",
                    c.name(), c.getDisplayName(), c.getTaxAuthority(), c.getCurrency(), escape(c.getAuthorityFullName())));
            if (i < countries.length - 1) sb.append(",");
        }
        sb.append("],\"documentTypes\":[");
        DocumentType[] types = DocumentType.values();
        for (int i = 0; i < types.length; i++) {
            DocumentType t = types[i];
            sb.append(String.format("{\"code\":\"%s\",\"name\":\"%s\",\"spanishName\":\"%s\",\"prefix\":\"%s\"}",
                    t.name(), t.getEnglishName(), t.getSpanishName(), t.getCodePrefix()));
            if (i < types.length - 1) sb.append(",");
        }
        sb.append("],\"formats\":[");
        DocumentFormat[] formats = DocumentFormat.values();
        for (int i = 0; i < formats.length; i++) {
            DocumentFormat f = formats[i];
            sb.append(String.format("{\"extension\":\"%s\",\"mimeType\":\"%s\",\"modern\":%b,\"binary\":%b}",
                    f.getExtension(), f.getMimeType(), f.isModernStandard(), f.isBinaryFormat()));
            if (i < formats.length - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    public static DocumentPayload parseDocumentPayload(String json) {
        Map<String, String> map = parseSimpleJsonMap(json);
        DocumentPayload.Builder builder = DocumentPayload.builder();

        if (map.containsKey("documentId")) builder.documentId(map.get("documentId"));
        if (map.containsKey("title")) builder.title(map.get("title"));
        if (map.containsKey("content")) builder.content(map.get("content"));
        if (map.containsKey("taxIdentifier")) builder.taxIdentifier(map.get("taxIdentifier"));
        
        if (map.containsKey("country")) {
            try {
                builder.country(Country.fromString(map.get("country")));
            } catch (Exception ignored) {}
        }
        if (map.containsKey("documentType")) {
            try {
                builder.documentType(DocumentType.fromString(map.get("documentType")));
            } catch (Exception ignored) {}
        }
        if (map.containsKey("format")) {
            try {
                builder.format(DocumentFormat.fromExtension(map.get("format")));
            } catch (Exception ignored) {}
        }
        if (map.containsKey("amount")) {
            try {
                builder.amount(Double.parseDouble(map.get("amount")));
            } catch (Exception ignored) {}
        }

        return builder.build();
    }

    public static List<DocumentPayload> parseDocumentPayloadList(String json) {
        List<DocumentPayload> list = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return list;

        // Extract items inside "[...]"
        int start = json.indexOf('[');
        int end = json.lastIndexOf(']');
        if (start < 0 || end < 0 || end <= start) return list;

        String inner = json.substring(start + 1, end).trim();
        List<String> objectStrings = splitJsonObjects(inner);
        for (String objStr : objectStrings) {
            list.add(parseDocumentPayload(objStr));
        }
        return list;
    }

    private static List<String> splitJsonObjects(String content) {
        List<String> list = new ArrayList<>();
        int depth = 0;
        int start = -1;
        boolean inQuote = false;
        char prev = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '"' && prev != '\\') {
                inQuote = !inQuote;
            } else if (!inQuote) {
                if (c == '{') {
                    if (depth == 0) start = i;
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0 && start != -1) {
                        list.add(content.substring(start, i + 1));
                        start = -1;
                    }
                }
            }
            prev = c;
        }
        return list;
    }

    private static Map<String, String> parseSimpleJsonMap(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null) return map;
        String clean = json.trim();
        if (clean.startsWith("{")) clean = clean.substring(1);
        if (clean.endsWith("}")) clean = clean.substring(0, clean.length() - 1);

        boolean inQuote = false;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean parsingKey = true;

        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);

            if (c == '\\' && i + 1 < clean.length()) {
                char next = clean.charAt(i + 1);
                if (next == '"' || next == '\\') {
                    // Skip backslash before quote or backslash
                    i++;
                    c = next;
                }
            }

            if (c == '"') {
                inQuote = !inQuote;
                continue;
            }

            if (!inQuote && c == ':') {
                parsingKey = false;
                continue;
            }

            if (!inQuote && c == ',') {
                storeEntry(map, currentKey.toString(), currentValue.toString());
                currentKey.setLength(0);
                currentValue.setLength(0);
                parsingKey = true;
                continue;
            }

            if (parsingKey) {
                currentKey.append(c);
            } else {
                currentValue.append(c);
            }
        }
        storeEntry(map, currentKey.toString(), currentValue.toString());
        return map;
    }

    private static void storeEntry(Map<String, String> map, String rawKey, String rawVal) {
        String k = rawKey.trim().replaceAll("^[\"\'\\\\]+|[\"\'\\\\]+$", "").trim();
        String v = rawVal.trim().replaceAll("^[\"\'\\\\]+|[\"\'\\\\]+$", "").trim();
        if (!k.isEmpty()) {
            map.put(k, v);
        }
    }

    private static void appendString(StringBuilder sb, String key, String val, boolean comma) {
        sb.append("\"").append(key).append("\":");
        if (val == null) {
            sb.append("null");
        } else {
            sb.append("\"").append(escape(val)).append("\"");
        }
        if (comma) sb.append(",");
    }

    private static void appendNumber(StringBuilder sb, String key, Number val, boolean comma) {
        sb.append("\"").append(key).append("\":").append(val != null ? val : 0);
        if (comma) sb.append(",");
    }

    private static void appendStringList(StringBuilder sb, String key, List<String> list, boolean comma) {
        sb.append("\"").append(key).append("\":[");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                sb.append("\"").append(escape(list.get(i))).append("\"");
                if (i < list.size() - 1) sb.append(",");
            }
        }
        sb.append("]");
        if (comma) sb.append(",");
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
