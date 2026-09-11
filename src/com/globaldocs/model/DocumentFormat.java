package com.globaldocs.model;

/**
 * Supported enterprise document file formats per workshop requirements:
 * .pdf, .doc, .docx, .md, .csv, .txt, .xlsx
 */
public enum DocumentFormat {
    PDF("pdf", "application/pdf", true, true),
    DOC("doc", "application/msword", false, true),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", true, true),
    MD("md", "text/markdown", true, false),
    CSV("csv", "text/csv", true, false),
    TXT("txt", "text/plain", true, false),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", true, true);

    private final String extension;
    private final String mimeType;
    private final boolean modernStandard;
    private final boolean binaryFormat;

    DocumentFormat(String extension, String mimeType, boolean modernStandard, boolean binaryFormat) {
        this.extension = extension;
        this.mimeType = mimeType;
        this.modernStandard = modernStandard;
        this.binaryFormat = binaryFormat;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public boolean isModernStandard() {
        return modernStandard;
    }

    public boolean isBinaryFormat() {
        return binaryFormat;
    }

    public static DocumentFormat fromExtension(String ext) {
        if (ext == null || ext.trim().isEmpty()) {
            throw new IllegalArgumentException("Extension cannot be null or empty");
        }
        String cleanExt = ext.trim().replaceAll("[\"\'\\\\]", "").toLowerCase();
        if (cleanExt.startsWith(".")) {
            cleanExt = cleanExt.substring(1);
        }
        for (DocumentFormat format : values()) {
            if (format.extension.equalsIgnoreCase(cleanExt)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported document format extension: " + ext);
    }
}
