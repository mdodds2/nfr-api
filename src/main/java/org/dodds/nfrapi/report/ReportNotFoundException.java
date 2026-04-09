package org.dodds.nfrapi.report;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException() {
        super("Report not found.");
    }
}
