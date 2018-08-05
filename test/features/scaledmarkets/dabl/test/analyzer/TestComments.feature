# language: en

@analyzer
Feature: TestComments
	
	@notdone
	Scenario: Single-line comment
		When one uses a single-line comment
		Then one can obtain the comment from the AST
	
	@notdone
	Scenario: Single-line syntactic comment
		When one uses a single-line syntactic comment
		Then one can obtain the comment as an annotation of the associated Node
	
	@notdone
	Scenario: Multi-line comment
		When one uses a multi-line comment
		Then one can obtain the comment from the AST
	
	@notdone
	Scenario: Multi-line syntactic comment
		When one uses a multi-line syntactic comment
		Then one can obtain the comment as an annotation of the associated Node
