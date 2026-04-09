package org.dodds.nfrapi.report;

public class DuplicateReportException extends RuntimeException {
    public DuplicateReportException() {
        super("Duplicate report name already exists.");
    }
}
