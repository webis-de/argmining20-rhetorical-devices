package de.aitools.ie.uima.io;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * An {@link InputStream} that can open {@link ClassLoader} resources, as well
 * as files.
 * 
 * @author michael.voelske@uni-weimar.de
 *
 */
public class ResourceInputStream extends InputStream implements Closeable, AutoCloseable {

	private InputStream wrapped;

	/**
	 * Creates an {@link InputStream} to the resource identified by the given
	 * path, which can be either a {@link ClassLoader} system resource, or an
	 * object on the local file system.
	 * 
	 * @param resourcePath
	 *            a path to a {@link ClassLoader} resource with the lexicon
	 *            file, or a path on the local file system. If the parameter can
	 *            be interpreted as either, {@link ClassLoader} resources take
	 *            precedence
	 * @throws FileNotFoundException
	 */
	public ResourceInputStream(String resourcePath) throws FileNotFoundException {
		wrapped = ClassLoader.getSystemResourceAsStream(resourcePath);
		if (wrapped == null) {
			String absolutePath = PathTools.getAbsolutePath(resourcePath);
			try {
				wrapped = new FileInputStream(absolutePath);
			} catch (Exception e) {
				throw new FileNotFoundException(String
						.format("Resource '%s' could not be located on the classpath or file system.", resourcePath));
			}
		}

	}

	/**
	 * @return
	 * @throws IOException
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		return wrapped.read();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return wrapped.hashCode();
	}

	/**
	 * @param b
	 * @return
	 * @throws IOException
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		return wrapped.read(b);
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return wrapped.equals(obj);
	}

	/**
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws IOException
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return wrapped.read(b, off, len);
	}

	/**
	 * @param n
	 * @return
	 * @throws IOException
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return wrapped.skip(n);
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return wrapped.toString();
	}

	/**
	 * @return
	 * @throws IOException
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		return wrapped.available();
	}

	/**
	 * @throws IOException
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		wrapped.close();
	}

	/**
	 * @param readlimit
	 * @see java.io.InputStream#mark(int)
	 */
	public void mark(int readlimit) {
		wrapped.mark(readlimit);
	}

	/**
	 * @throws IOException
	 * @see java.io.InputStream#reset()
	 */
	public void reset() throws IOException {
		wrapped.reset();
	}

	/**
	 * @return
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported() {
		return wrapped.markSupported();
	}

}
