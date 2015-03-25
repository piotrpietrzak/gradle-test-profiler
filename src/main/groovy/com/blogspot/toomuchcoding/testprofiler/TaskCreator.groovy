package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.Task

import static com.blogspot.toomuchcoding.testprofiler.TestProfilerPlugin.PROFILE_TESTS_TASK_NAME
import static com.blogspot.toomuchcoding.testprofiler.TestProfilerPlugin.PROFILE_TESTS_REPORT_TASK_NAME

@PackageScope
@Slf4j
class TaskCreator {

    Task buildReportMergerForProject(Project project, TestProfilerPluginExtension extension) {
        Task task = createTask(extension, project)
        if (!task) {
            return null
        }
        task.group = 'Verification'
        task.description = "Creates a report of tests execution time"
        return task
    }

    private Task createTask(TestProfilerPluginExtension extension, Project project) {
        if (extension.enabled) {
            def reportMergerTask = createReportMerger(project, extension)
            createReportTask(project,extension)
            return reportMergerTask
        } else {
            return createNoOpTask(project)
        }
    }

    private ReportMergerTask createReportMerger(Project project, TestProfilerPluginExtension extension) {
        if (taskAlreadyExists(project)) {
            return null
        }
        ReportMergerTask reportMerger = project.tasks.create(PROFILE_TESTS_TASK_NAME, ReportMergerTask)
        log.info("Created a task [$PROFILE_TESTS_TASK_NAME] in root project")
        reportMerger.conventionMapping.with {
            testProfilerPluginExtension = { extension }
            mergedTestProfilingSummaryFile = { extension.mergedCsvSummaryPath }
        }
        return reportMerger
    }

    private ReportMergerTask createReportTask(Project project, TestProfilerPluginExtension extension) {
        if (reportTaskAlreadyExists(project)) {
            return null
        }

    }

    private Set<Task> taskAlreadyExists(Project project) {
        return project.getTasksByName(PROFILE_TESTS_TASK_NAME, false)
    }

    private Set<Task> reportTaskAlreadyExists(Project project) {
        return project.getTasksByName(PROFILE_TESTS_REPORT_TASK_NAME, false)
    }

    private NoOpTask createNoOpTask(Project project) {
        return project.tasks.create(PROFILE_TESTS_TASK_NAME, NoOpTask)
    }
}
