package scaledmarkets.dabl.util;

import scaledmarkets.dabl.node.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.List;

public class Utilities {
	
	public static String PropertyFileName = ".dabl.properties";
	
	public static String createNameFromPath(List<TId> path) {
		String name = "";
		boolean firstTime = true;
		for (TId id : path) {
			if (firstTime) firstTime = false;
			else name = name + ".";
			name = name + id.getText();
		}
		return name;
	}

	/**
	 * If expr is false, throw an Exception.
	 */
	public static void assertThat(boolean expr) {
		if (! expr) throw new RuntimeException("Assertion violation");
	}
	
	/**
	 * If expr is false, print the message and throw an Exception.
	 */
	public static void assertThat(boolean expr, String msg) {
		if (msg == null) msg = "";
		if (msg != null) msg = "; " + msg;
		if (! expr) throw new RuntimeException("Assertion violation: " + msg);
	}
	
	/**
	 * If expr is false, perform the specified action and then throw an Exception.
	 */
	public static void assertThat(boolean expr, Runnable action) {
		if (! expr) {
			System.out.println("Assertion violation");
			action.run();
			throw new RuntimeException("Assertion violation");
		}
	}
	
	/**
	 * Recursively delete the specified directory and all contents.
	 */
	public static void deleteDirectoryTree(Path root) throws Exception {
		
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
			
			public FileVisitResult postVisitDirectory(Path dir, IOException e)
			throws IOException {
				if (e == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					// directory iteration failed
					throw e;
				}
			}
		});
	}
	
	/**
	 * Obtain a DABL configuration setting. Obtain it first from the environment;
	 * if not found, then look for a .dabl.properties file in the current directory
	 * and then in the user's home directory.
	 */
	public static String getSetting(String name) throws IOException {
		
		// Check environment.
		String value = System.getenv(name);
		if (value != null) return value;
		
		// Check current directory for a properties file.
		String dirstr = System.getProperty("user.dir");
		if (dirstr == null) throw new RuntimeException("No current working directory");
		File curdir = new File(dirstr);
		if (! curdir.exists()) throw new RuntimeException("Current directory not found");
		File curdirPropertyFile = new File(curdir, PropertyFileName);
		if (curdirPropertyFile.exists()) {
			Properties properties = new Properties();
			properties.load(new FileReader(curdirPropertyFile));
			value = properties.getProperty(name);
			if ((value != null) && (value.equals(""))) return value;
		}
		
		// Check user's home directory for a properties file.
		String homestr = System.getProperty("user.home");
		if (homestr == null) throw new RuntimeException("No user home");
		File home = new File(homestr);
		if (! home.exists()) throw new RuntimeException("User home directory not found");
		File homePropertyFile = new File(home, PropertyFileName);
		if (homePropertyFile.exists()) {
			Properties properties = new Properties();
			properties.load(new FileReader(homePropertyFile));
			value = properties.getProperty(name);
			if ((value != null) && (value.equals(""))) return value;
		}
		
		// Setting was not found.
		return null;
	}
}
