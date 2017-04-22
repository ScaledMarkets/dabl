package scaledmarkets.dabl.analysis;

import scaledmarkets.dabl.node.*;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileNotFoundException;

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
	 * if not found, then look for a .dabl.properties file in the user's home directory.
	 */
	public static String getSetting(String name) throws IOException {
		
		String value = System.getenv(name);
		if (value != null) return value;
		
		String homestr = System.getProperty("user.home");
		if (homestr == null) throw new RuntimeException("No user home");
		File home = new File(homestr);
		if (! home.exists()) throw new RuntimeException("User home directory not found");
		
		Properties properties = new Properties();
		File propertyFile = new File(home, PropertyFileName);
		properties.load(new FileReader(propertyFile));
		
		return properties.getProperty(name);
	}
}
