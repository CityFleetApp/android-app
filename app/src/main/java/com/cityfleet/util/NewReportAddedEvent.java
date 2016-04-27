package com.cityfleet.util;

import com.cityfleet.model.Report;

/**
 * Created by vika on 08.04.16.
 */
public class NewReportAddedEvent {
    private Report report;

    public NewReportAddedEvent(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
    }
}
