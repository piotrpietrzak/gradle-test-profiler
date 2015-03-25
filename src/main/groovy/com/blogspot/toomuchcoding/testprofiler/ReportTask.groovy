package com.blogspot.toomuchcoding.testprofiler
import org.gradle.api.DefaultTask
import org.gradle.api.reporting.Reporting
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.reflect.Instantiator
import org.gradle.logging.ConsoleRenderer

import javax.inject.Inject

@ParallelizableTask
class ReportTask extends DefaultTask implements Reporting<TestProfilerReports> {

    @Inject
    TestProfilerPluginExtension testProfilerPluginExtension

    @Nested
    private final TestProfilerReportsImpl reports

    ReportTask() {
        reports = instantiator.newInstance(TestProfilerReportsImpl, this)
    }

    @Inject
    Instantiator getInstantiator() {
        throw new UnsupportedOperationException();
    }

    @TaskAction
    def run() {
        reports.collect {
            def reportUrl = new ConsoleRenderer().asClickableFileUrl(it.destination)
            logger.info("See the report at: $reportUrl $it")
        }
        logger.info("Enabled: ${reports.enabled} ")
        reports.enabled.collect {
            if(it.destination !=null) {
                logger.info("destination set for ${it}")
            }
        }
    }


    @Override
    TestProfilerReports getReports() {
        reports
    }

    @Override
    TestProfilerReports reports(Closure closure) {
        reports.configure(closure)
    }
}
