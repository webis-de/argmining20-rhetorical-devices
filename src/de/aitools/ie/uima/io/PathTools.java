package de.aitools.ie.uima.io;


import java.io.File;
import java.net.URL;

/**
 * This class contains some public static methods for converting paths somehow, 
 * for instance changing relative into absolute paths.
 * 
 * @author henning.wachsmuth
 *
 */
public class PathTools {

	/**
	 * Returns the absolute path for the given path. Works for both local
	 * and online usage of the given class.
	 * 
	 * @param path The path
	 * @return The absolute path
	 */
	public static String getAbsolutePath(String path){
		if (new File(path).getAbsoluteFile().exists()){
			return new File(path).getAbsolutePath();
		} else {
			final URL url = ClassLoader.getSystemResource(path);
			if (url == null) {
				throw new NullPointerException("Path does not exist: " + path);
			}
			return url.getPath();
		}
	}	
}
