package org.meros.pb4mina;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link BoundedInputStream} */
class BoundedInputStreamTest {

  @Test
  void testReadSingleByte() throws IOException {
    byte[] data = {1, 2, 3, 4, 5};
    ByteArrayInputStream baseStream = new ByteArrayInputStream(data);
    BoundedInputStream bounded = new BoundedInputStream(baseStream, 3);

    assertEquals(1, bounded.read());
    assertEquals(2, bounded.read());
    assertEquals(3, bounded.read());
    assertEquals(-1, bounded.read()); // Should return -1 after limit
  }

  @Test
  void testReadByteArray() throws IOException {
    byte[] data = {1, 2, 3, 4, 5};
    ByteArrayInputStream baseStream = new ByteArrayInputStream(data);
    BoundedInputStream bounded = new BoundedInputStream(baseStream, 3);

    byte[] buffer = new byte[10];
    int bytesRead = bounded.read(buffer);

    assertEquals(3, bytesRead);
    assertEquals(1, buffer[0]);
    assertEquals(2, buffer[1]);
    assertEquals(3, buffer[2]);
  }

  @Test
  void testReadByteArrayWithOffset() throws IOException {
    byte[] data = {1, 2, 3, 4, 5};
    ByteArrayInputStream baseStream = new ByteArrayInputStream(data);
    BoundedInputStream bounded = new BoundedInputStream(baseStream, 3);

    byte[] buffer = new byte[10];
    int bytesRead = bounded.read(buffer, 2, 5);

    assertEquals(3, bytesRead);
    assertEquals(0, buffer[0]);
    assertEquals(0, buffer[1]);
    assertEquals(1, buffer[2]);
    assertEquals(2, buffer[3]);
    assertEquals(3, buffer[4]);
  }

  @Test
  void testAvailable() throws IOException {
    byte[] data = {1, 2, 3, 4, 5};
    ByteArrayInputStream baseStream = new ByteArrayInputStream(data);
    BoundedInputStream bounded = new BoundedInputStream(baseStream, 3);

    assertEquals(3, bounded.available());
    bounded.read();
    assertEquals(2, bounded.available());
    bounded.read();
    assertEquals(1, bounded.available());
    bounded.read();
    assertEquals(0, bounded.available());
  }

  @Test
  void testMarkNotSupported() {
    byte[] data = {1, 2, 3};
    ByteArrayInputStream baseStream = new ByteArrayInputStream(data);
    BoundedInputStream bounded = new BoundedInputStream(baseStream, 3);

    assertFalse(bounded.markSupported());
  }

  @Test
  void testLengthLargerThanAvailableThrows() {
    byte[] data = {1, 2, 3};
    ByteArrayInputStream baseStream = new ByteArrayInputStream(data);

    assertThrows(RuntimeException.class, () -> new BoundedInputStream(baseStream, 10));
  }

  @Test
  void testZeroLengthBound() throws IOException {
    byte[] data = {1, 2, 3};
    ByteArrayInputStream baseStream = new ByteArrayInputStream(data);
    BoundedInputStream bounded = new BoundedInputStream(baseStream, 0);

    assertEquals(0, bounded.available());
    assertEquals(-1, bounded.read());
  }

  @Test
  void testReadReturnsMinusOneAfterExhausted() throws IOException {
    byte[] data = {1, 2};
    ByteArrayInputStream baseStream = new ByteArrayInputStream(data);
    BoundedInputStream bounded = new BoundedInputStream(baseStream, 2);

    byte[] buffer = new byte[10];
    bounded.read(buffer);

    // Further reads should return -1
    assertEquals(-1, bounded.read());
    assertEquals(-1, bounded.read(buffer));
    assertEquals(-1, bounded.read(buffer, 0, 5));
  }
}
