package com.citifleet.util;

import com.citifleet.model.Report;

/**
 * Created by vika on 08.04.16.
 */
public class ReportDeletedEvent {
    private Report report;

    public ReportDeletedEvent(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
    }
}
