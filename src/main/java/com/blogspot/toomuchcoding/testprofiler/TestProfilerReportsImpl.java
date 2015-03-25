package com.blogspot.toomuchcoding.testprofiler;

import org.gradle.api.Task;
import org.gradle.api.reporting.SingleFileReport;
import org.gradle.api.reporting.internal.TaskGeneratedSingleFileReport;
import org.gradle.api.reporting.internal.TaskReportContainer;

public class TestProfilerReportsImpl extends TaskReportContainer<SingleFileReport> implements TestProfilerReports {

    public TestProfilerReportsImpl(Task task) {
        super(SingleFileReport.class, task);

        add(TaskGeneratedSingleFileReport.class, "html", task);
        add(TaskGeneratedSingleFileReport.class, "csv", task);
    }

    @Override
    public SingleFileReport getHtml() {
        return getByName("html");
    }

    @Override
    public SingleFileReport getCsv() {
        return getByName("csv");
    }
}
