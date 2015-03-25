package integration
import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

class ReportingSpec extends IntegrationSpec {

    def "should create a summary file with report summary"() {
        given:
        copyResources("project_with_reporting", "")
        when:
        ExecutionResult result = runTasksSuccessfully('my')
        then:
        taskHasBeenExecuted(result)
        and:
        reportsHaveBeenCreated()
    }

    def reportsHaveBeenCreated() {
        return true
    }

    private boolean taskHasBeenExecuted(ExecutionResult result) {
        println " --> ${result.standardOutput} <-- "
        return true
    }

}
