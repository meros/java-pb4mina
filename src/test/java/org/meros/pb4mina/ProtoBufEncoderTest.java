package org.meros.pb4mina;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.Message;
import java.io.InvalidObjectException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Unit tests for {@link ProtoBufEncoder} */
class ProtoBufEncoderTest {

  private ProtoBufEncoder encoder;
  private IoSession mockSession;
  private ProtocolEncoderOutput mockOutput;

  @BeforeEach
  void setUp() {
    encoder = new ProtoBufEncoder();
    mockSession = mock(IoSession.class);
    mockOutput = mock(ProtocolEncoderOutput.class);
  }

  @Test
  void testEncodeValidMessage() throws Exception {
    // Create a simple test message using ByteString which is a Message
    Message testMessage = createTestMessage();

    ArgumentCaptor<IoBuffer> bufferCaptor = ArgumentCaptor.forClass(IoBuffer.class);

    encoder.encode(mockSession, testMessage, mockOutput);

    verify(mockOutput).write(bufferCaptor.capture());

    IoBuffer capturedBuffer = bufferCaptor.getValue();
    assertNotNull(capturedBuffer);
    assertTrue(capturedBuffer.remaining() > 4); // At least 4 bytes for length header

    // Verify the length header
    CodedInputStream cis = CodedInputStream.newInstance(capturedBuffer.asInputStream());
    int length = cis.readFixed32();
    assertEquals(testMessage.getSerializedSize(), length);
  }

  @Test
  void testEncodeNonMessageThrows() {
    String invalidObject = "not a protobuf message";

    assertThrows(
        InvalidObjectException.class, () -> encoder.encode(mockSession, invalidObject, mockOutput));
  }

  @Test
  void testEncodeNullObjectThrows() {
    assertThrows(Exception.class, () -> encoder.encode(mockSession, null, mockOutput));
  }

  /**
   * Creates a simple test protobuf message for testing. Uses a dynamically created message with
   * some fields.
   */
  private Message createTestMessage() {
    // Create a simple message using the Any type or a custom builder
    return com.google.protobuf.Any.pack(com.google.protobuf.StringValue.of("test message content"));
  }
}
