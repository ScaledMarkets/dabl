package com.scaledmarkets.dabl.test.analyzer;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.scaledmarkets.dabl.client.*;
import com.scaledmarkets.dabl.analyzer.*;
import com.scaledmarkets.dabl.node.*;
import com.scaledmarkets.dabl.test.TestBase;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.LinkedList;

public class TestSingleLetterId extends TestBase {
	
	@When("^a task name is a single character$")
	public void a_namespace_decl_ends_with_a_newline() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" + // no space between 'simple' and the newlilne
"task t\n" +
"  when true\n" +
"  abc = f true"
			);
		
		Dabl dabl = new Dabl(false, true, true, reader);
		createHelper(dabl.process());
	}
}
