package com.blogspot.toomuchcoding.testprofiler

import org.gradle.api.reporting.ReportContainer
import org.gradle.api.reporting.SingleFileReport

public interface TestProfilerReports extends ReportContainer<SingleFileReport> {
    /**
     * The test profiler HTML report
     *
     * @return The test profiler HTML report
     */
    SingleFileReport getHtml();

    /**
     * The test profiler CSV report
     *
     * @return The test profiler CSV report
     */
    SingleFileReport getCsv();

}
