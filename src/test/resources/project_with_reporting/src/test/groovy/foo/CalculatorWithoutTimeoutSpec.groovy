package project_with_reporting.src.test.groovy.foo

import spock.lang.Specification

class CalculatorWithoutTimeoutSpec extends Specification {

    def 'should add two numbers'() throws Exception {
        expect:
            Thread.sleep(100)
    }
}
