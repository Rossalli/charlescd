package io.charlescd.circlematcher.domain.validation

import io.charlescd.circlematcher.domain.Content
import io.charlescd.circlematcher.domain.LogicalOperatorType
import io.charlescd.circlematcher.domain.Node
import io.charlescd.circlematcher.domain.NodeType
import io.charlescd.circlematcher.utils.TestUtils
import spock.lang.Specification

class NodeValidationTest extends Specification {

    def "Validate simple RULE node type"() {
        given:
        def value = "user@zup.com.br"
        def values = new ArrayList()
        values.add(value)

        def content = TestUtils.createContent(values)
        def node = TestUtils.createNode(content)
        when:
        def validator = new NodeValidator()
        def valid = validator.isValid(node, null)
        then:
        assert valid
    }

    def "Validate CLAUSE node with simple RULE type"() {
        given:
        def value = "user@zup.com.br"
        def values = new ArrayList()
        values.add(value)

        def content = TestUtils.createContent(values)
        def clauses = [new Node(NodeType.RULE, null, null, content)]
        def node = new Node(NodeType.CLAUSE, LogicalOperatorType.OR, clauses, null)
        when:
        def validator = new NodeValidator()
        def valid = validator.isValid(node, null)
        then:
        assert valid
    }

    def "Invalid CLAUSE node without simple RULE type"() {
        given:
        def value = "user@zup.com.br"
        def values = new ArrayList()
        values.add(value)

        def node = new Node(NodeType.CLAUSE, LogicalOperatorType.OR, null, null)
        when:
        def validator = new NodeValidator()
        def valid = validator.isValid(node, null)
        then:
        assert !valid
    }

    def "Validate CLAUSE node with deep RULE type"() {
        given:
        def ruleName1 = new Node(NodeType.RULE, null, null, new Content("name", "EQUAL", ["tester1"]))
        def ruleAge = new Node(NodeType.RULE, null, null, new Content("age", "EQUAL", ["12"]))
        def ruleName2 = new Node(NodeType.RULE, null, null, new Content("name", "EQUAL", ["tester"]))

        def nameAndAge = new Node(NodeType.CLAUSE, LogicalOperatorType.AND, [ruleName1, ruleAge], null)
        def name = new Node(NodeType.CLAUSE, LogicalOperatorType.OR, [ruleName2], null)

        def node = new Node(NodeType.CLAUSE, LogicalOperatorType.OR, [nameAndAge, name], null)
        when:
        def validator = new NodeValidator()
        def valid = validator.isValid(node, null)
        then:
        assert valid
    }
}