package scaledmarkets.dabl.test;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.helper.*;
import scaledmarkets.dabl.node.*;

import java.util.List;

/**
 * Utilities shared by the DABL Cucumber test suite.
 * Ref for JVM hooks: http://zsoltfabok.com/blog/2012/09/cucumber-jvm-hooks/
 */
public class TestBase {

	protected Helper helper;
	
	public TestBase() {
	}
	
	protected Helper createHelper(CompilerState state) {
		this.helper = new Helper(state);
		return this.helper;
	}
	
	protected Helper getHelper() {
		return this.helper;
	}
	
	protected CompilerState getState() {
		return this.helper.getState();
	}
	
	public void assertThat(boolean expr) throws Exception {
		this.helper.assertThat(expr);
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
	 * Delete all of the files and subdirectories in the specified directory.
	 */
	protected void deleteDirContents(File dir) throws Exception {
		if (! file.isDirectory()) throw new Exception("File " + dir.toString() + " is not a direcrtory");
		Stream<Path> paths = Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path> () {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
			
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (e == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					throw e; // directory iteration failed
				}
			}
		}
	}
}
