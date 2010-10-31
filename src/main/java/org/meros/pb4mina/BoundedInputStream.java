package org.meros.pb4mina;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * Input stream that wraps another input stream and sets a limit of number of bytes that is readable from the original stream
 * 
 * @author meros
 *
 */
public class BoundedInputStream extends InputStream {
	
	private InputStream inputStream;
	private int diff;

	public BoundedInputStream(InputStream inputStream, int length) {
		try {
			if (length > inputStream.available())
				throw new RuntimeException("You need to specify a length smaller or equal to the bytes available in inputStream");

			this.inputStream = inputStream;
			this.diff = inputStream.available()-length;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int available() throws IOException {
		return inputStream.available()-diff;
	}
	
	@Override
	public boolean markSupported() {
		return false;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		if (inputStream.available()-diff == 0)
			return -1;
		
		return inputStream.read(b, 0, Math.min(b.length, inputStream.available()-diff));
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (inputStream.available()-diff == 0)
			return -1;
		
		return inputStream.read(b, off, Math.min(len, inputStream.available()-diff));
	}

	@Override
	public int read() throws IOException {		
		if (inputStream.available()-diff == 0)
			return -1;
		
		return inputStream.read();
	}
}
