package org.meros.pb4mina;

import static org.junit.jupiter.api.Assertions.*;

import com.google.protobuf.Any;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ProtoBufCoderFilter} */
class ProtoBufCoderFilterTest {

  @Test
  void testConstructor() {
    ProtoBufMessageFactory messageFactory = () -> Any.newBuilder();

    // Should create without throwing
    ProtoBufCoderFilter filter = new ProtoBufCoderFilter(messageFactory);

    assertNotNull(filter);
  }

  @Test
  void testConstructorWithNullFactory() {
    // Factory can be null in constructor but will fail later when used
    // This is consistent with how the code works - it doesn't validate null
    ProtoBufCoderFilter filter = new ProtoBufCoderFilter(null);
    assertNotNull(filter);
  }
}
