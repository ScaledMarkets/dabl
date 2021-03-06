package com.scaledmarkets.dabl.test;

import com.scaledmarkets.dabl.analyzer.*;
import com.scaledmarkets.dabl.exec.ExecHelper;
import com.scaledmarkets.dabl.node.*;
import com.scaledmarkets.dabl.util.Utilities;

import java.util.List;
import java.util.stream.Stream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Utilities shared by the DABL Cucumber test suite.
 * Ref for JVM hooks: http://zsoltfabok.com/blog/2012/09/cucumber-jvm-hooks/
 */
public class TestBase {

	protected ExecHelper helper;
	
	public TestBase() {
	}
	
	protected ExecHelper createHelper(CompilerState state) {
		this.helper = new ExecHelper(state);
		return this.helper;
	}
	
	protected ExecHelper getHelper() {
		return this.helper;
	}
	
	protected CompilerState getState() {
		return this.helper.getState();
	}
	
	public void assertThat(boolean expr, String msg) throws Exception {
		this.helper.assertThat(expr, msg);
	}
	
	public void assertThat(boolean expr, Runnable action) throws Exception {
		this.helper.assertThat(expr, action);
	}
	
	public void msg(String message) {
		System.out.println(message);
	}
	
	public void printAST(String title) {
		this.helper.printAST(title);
	}
	
	/**
	 * Delete all of the files and subdirectories in the specified directory, as
	 * well as the specified directory.
	 */
	protected void deleteDirectoryTree(File dir) throws Exception {
		Utilities.deleteDirectoryTree(dir);
	}
	
	/**
	 * 
	 */
	protected void printDirectoryTree(File dir) throws Exception {
		Utilities.printDirectoryTree(dir);
	}
	
	/**
	 * 
	 */
	protected void operateOnDirectoryTree(File dir, Utilities.FileOperator operator) throws Exception {
		Utilities.operateOnDirectoryTree(dir, operator);
	}
}
