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
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Utilities {
	
	public static String PropertyFileName = ".dabl.properties";
	public static String ContainerPropertyFileName = ".dabl.container.properties";
	
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
			System.out.println("Assertion violation; performing diagnostic action...");
			try {
				action.run();
			} finally {
				System.out.println();
				System.out.println("End of diagnostic action.");
			}
			throw new RuntimeException("Assertion violation");
		}
	}

	public interface FileOperator {
		void operateOnFile(Path f) throws IOException;
	}
	
	/**
	 * Delete all of the files and subdirectories in the specified directory, as
	 * well as the specified directory.
	 */
	public static void deleteDirectoryTree(File dir) throws Exception {
		operateOnDirectoryTree(dir, new FileOperator() {
			public void operateOnFile(Path f) throws IOException {
				Files.delete(f);
			}
		});
	}
	
	/**
	 * 
	 */
	public static void printDirectoryTree(File dir) throws Exception {
		operateOnDirectoryTree(dir, new FileOperator() {
			public void operateOnFile(Path f) throws IOException {
				System.out.println(f.toString());
			}
		});
	}
	
	/**
	 * 
	 */
	public static void operateOnDirectoryTree(File dir, FileOperator operator) throws Exception {
		if (! dir.exists()) return;
		if (! dir.isDirectory()) throw new Exception("File " + dir.toString() + " is not a direcrtory");
		Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path> () {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				operator.operateOnFile(file);
				return FileVisitResult.CONTINUE;
			}
			
			public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
				if (exc == null) {
					operator.operateOnFile(d);
					return FileVisitResult.CONTINUE;
				} else {
					throw exc; // directory iteration failed
				}
			}
		});
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
	 * and then in the user's home directory. If that fails, look for the properties
	 * file in the classpath. If that fails, return null.
	 */
	public static String getSetting(String name) throws Exception {
		
		// Check environment.
		String value = System.getenv(name);
		if (value != null) return value;
		
		// Check current directory for a properties file.
		Properties curDirProperties = getCurDirProperties(PropertyFileName);
		if (curDirProperties != null) {
			value = curDirProperties.getProperty(name);
			if ((value != null) && (! value.equals(""))) return value;
		}
		
		// Check user's home directory for a properties file.
		Properties homeProperties = getUserHomeProperties(PropertyFileName);
		if (homeProperties != null) {
			value = homeProperties.getProperty(name);
			if ((value != null) && (! value.equals(""))) return value;
		}
		
		// Check classpath for a properties file.
		Properties classpathProperties = getClasspathProperties(PropertyFileName);
		if (classpathProperties != null) {
			value = classpathProperties.getProperty(name);
			if ((value != null) && (! value.equals(""))) return value;
		}
		
		// Setting was not found.
		return null;
	}
	
	/**
	 * Assemble a map of the container runtime properties. They may be specified in all
	 * of the same manner as DABL properties, with the same precedence rules.
	 */
	public static Properties getContainerProperties() throws Exception {
		
		Properties properties = new Properties();
		
		// Load settings from the classpath first - these have the lowest precedence.
		Properties classpathProperties = getClasspathProperties(ContainerPropertyFileName);
		if (classpathProperties != null) properties.putAll(classpathProperties);
		
		// Next load settings from the user home directory.
		Properties homeProperties = getUserHomeProperties(ContainerPropertyFileName);
		if (homeProperties != null) properties.putAll(homeProperties);
		
		// Finally, load settings from the current directory - these have the
		// highest precedence.
		Properties curDirProperties = getCurDirProperties(ContainerPropertyFileName);
		if (curDirProperties != null) properties.putAll(curDirProperties);
		
		return properties;
	}
	
	protected static Properties getCurDirProperties(String propertyFileName) throws Exception {
		
		String dirstr = System.getProperty("user.dir");
		if (dirstr == null) throw new RuntimeException("No current working directory");
		File curdir = new File(dirstr);
		if (! curdir.exists()) throw new RuntimeException("Current directory not found");
		File curdirPropertyFile = new File(curdir, PropertyFileName);
		if (curdirPropertyFile.exists()) {
			Properties properties = new Properties();
			properties.load(new FileReader(curdirPropertyFile));
			return properties;
		}
		return null;
	}
	
	protected static Properties getUserHomeProperties(String propertyFileName) throws Exception {
		
		String homestr = System.getProperty("user.home");
		if (homestr == null) throw new RuntimeException("No user home");
		File home = new File(homestr);
		if (! home.exists()) throw new RuntimeException("User home directory not found");
		File homePropertyFile = new File(home, PropertyFileName);
		if (homePropertyFile.exists()) {
			Properties properties = new Properties();
			properties.load(new FileReader(homePropertyFile));
			return properties;
		}
		return null;
	}
	
	protected static Properties getClasspathProperties(String propertyFileName) {
		
		try {
			Properties properties = new Properties();
			InputStream is = Utilities.class.getResourceAsStream("/" + PropertyFileName);
			properties.load(is);
			return properties;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * 
	 */
	public static void pipeInputStreamToOutputStream(InputStream istream, OutputStream ostream)
	throws IOException {
		
		byte[] buffer = new byte[1024];
		while(true) {
			int n = istream.read(buffer);
			if (n <= 0) break;
			ostream.write(buffer, 0, n);
		}
	}
}
