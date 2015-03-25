package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.inject.Inject

@CompileStatic
@Slf4j
class TestProfilerPlugin implements Plugin<Project> {

    public static final String PROFILE_TESTS_TASK_NAME = "profileTests"
    public static final String PROFILE_TESTS_REPORT_TASK_NAME = "profileTestReports"
    public static final String TIMEOUT_ADDER_TESTS_TASK_NAME = "addTimeout"

    @PackageScope static final String DEFAULT_TEST_TIMEOUT_PROPERTY = 'default.test.timeout'

    private static final String DEFAULT_REPORTS_FOLDER = '/reports/test_profiling'
    private static final String DEFAULT_CSV_REPORT_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/testsProfile.csv"
    private static final String DEFAULT_HTML_REPORT_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/testsProfile.html"
    private static final String DEFAULT_CSV_MERGED_REPORTS_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/summary.csv"
    private static final String DEFAULT_HTML_MERGED_REPORTS_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/summary.html"

    private TestTaskModifier testTaskModifier
    private final LoggerProxy loggerProxy
    private final ExtensionCreator extensionCreator
    private final TaskCreator taskCreator

    @Inject
    TestProfilerPlugin() {
        this.loggerProxy = new LoggerProxy()
        this.extensionCreator = new ExtensionCreator()
        this.taskCreator = new TaskCreator()
    }

    protected TestProfilerPlugin(TestTaskModifier testTaskModifier, LoggerProxy loggerProxy, ExtensionCreator extensionCreator) {
        this.testTaskModifier = testTaskModifier
        this.loggerProxy = loggerProxy
        this.extensionCreator = extensionCreator
        this.taskCreator = new TaskCreator()
    }

    void apply(Project project) {
        TestProfilerPluginExtension extension = extensionCreator.createExtension(project)
        setDefaults(project, extension)
        printExtensionValues(extension)
        modifyTestTasks(project, extension)
        createSummaryReportTask(project, extension)
        performBuildBreakingLogic(project, extension)
    }

    private void printExtensionValues(TestProfilerPluginExtension testProfilerPluginExtension) {
        loggerProxy.debug("Setting up profiling with the following default parameters [$testProfilerPluginExtension]")
    }

    private void setDefaults(Project project, TestProfilerPluginExtension testProfilerPluginExtension) {
        testProfilerPluginExtension.relativeCsvReportPath = new File(DEFAULT_CSV_REPORT_RELATIVE_PATH)
        testProfilerPluginExtension.relativeHtmlReportPath = new File(DEFAULT_HTML_REPORT_RELATIVE_PATH)
        testProfilerPluginExtension.mergedCsvSummaryPath = new File(project.rootProject.buildDir, DEFAULT_CSV_MERGED_REPORTS_RELATIVE_PATH)
        testProfilerPluginExtension.mergedHtmlSummaryPath = new File(project.rootProject.buildDir, DEFAULT_HTML_MERGED_REPORTS_RELATIVE_PATH)
    }

    private void modifyTestTasks(Project project, TestProfilerPluginExtension extension) {
        File mergedTestProfilingSummaryDir = mergedTestProfilingSummaryDir(extension)
        new TestTaskModifier(mergedTestProfilingSummaryDir, project, extension).modifyCurrentTestTasks()
    }

    private void createSummaryReportTask(Project project, TestProfilerPluginExtension extension) {
        taskCreator.buildReportMergerForProject(project.rootProject, extension)
    }

    private File mergedTestProfilingSummaryDir(TestProfilerPluginExtension extension) {
        File mergedTestProfilingSummaryDir = extension.mergedCsvSummaryPath.parentFile
        mergedTestProfilingSummaryDir.mkdirs()
        return mergedTestProfilingSummaryDir
    }

    private performBuildBreakingLogic(Project project, TestProfilerPluginExtension extension) {
        new BuildBreaker(project, extension, loggerProxy).performBuildBreakingLogic()
    }
}
