package org.meros.pb4mina;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.protobuf.Any;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import java.io.ByteArrayOutputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Unit tests for {@link ProtoBufDecoder} */
class ProtoBufDecoderTest {

  private ProtoBufDecoder decoder;
  private IoSession mockSession;
  private ProtocolDecoderOutput mockOutput;
  private ProtoBufMessageFactory messageFactory;

  @BeforeEach
  void setUp() {
    // Create a factory that creates Any message builders
    messageFactory = () -> Any.newBuilder();
    decoder = new ProtoBufDecoder(messageFactory);
    mockSession = mock(IoSession.class);
    mockOutput = mock(ProtocolDecoderOutput.class);
  }

  @Test
  void testDecodeCompleteMessage() throws Exception {
    // Create a test message
    Any testMessage = Any.pack(StringValue.of("test content"));

    // Encode the message with length prefix
    IoBuffer buffer = createEncodedBuffer(testMessage);

    // Decode the message
    boolean result = decoder.doDecode(mockSession, buffer, mockOutput);

    // Verify message was decoded
    assertTrue(result);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    verify(mockOutput).write(messageCaptor.capture());

    Message decodedMessage = messageCaptor.getValue();
    assertNotNull(decodedMessage);
    assertTrue(decodedMessage instanceof Any);
    assertEquals(testMessage, decodedMessage);
  }

  @Test
  void testDecodeIncompleteLength() throws Exception {
    // Only 2 bytes when 4 are needed for length
    IoBuffer buffer = IoBuffer.allocate(2);
    buffer.put((byte) 0);
    buffer.put((byte) 0);
    buffer.flip();

    boolean result = decoder.doDecode(mockSession, buffer, mockOutput);

    assertFalse(result);
    verify(mockOutput, never()).write(any());
  }

  @Test
  void testDecodeIncompleteMessage() throws Exception {
    Any testMessage = Any.pack(StringValue.of("test content"));
    int messageSize = testMessage.getSerializedSize();

    // Create buffer with full length header but only partial message
    IoBuffer buffer = IoBuffer.allocate(4 + messageSize / 2);
    CodedOutputStream cos = CodedOutputStream.newInstance(buffer.asOutputStream());
    cos.writeFixed32NoTag(messageSize);
    cos.flush();

    // Write only half the message bytes
    byte[] messageBytes = testMessage.toByteArray();
    buffer.put(messageBytes, 0, messageSize / 2);
    buffer.flip();

    boolean result = decoder.doDecode(mockSession, buffer, mockOutput);

    assertFalse(result);
    verify(mockOutput, never()).write(any());
  }

  @Test
  void testDecodeMultipleMessages() throws Exception {
    Any testMessage1 = Any.pack(StringValue.of("message one"));
    Any testMessage2 = Any.pack(StringValue.of("message two"));

    // Create buffer with two encoded messages
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CodedOutputStream cos = CodedOutputStream.newInstance(baos);

    // First message
    cos.writeFixed32NoTag(testMessage1.getSerializedSize());
    testMessage1.writeTo(cos);

    // Second message
    cos.writeFixed32NoTag(testMessage2.getSerializedSize());
    testMessage2.writeTo(cos);

    cos.flush();

    IoBuffer buffer = IoBuffer.wrap(baos.toByteArray());

    // Decode first message
    boolean result1 = decoder.doDecode(mockSession, buffer, mockOutput);
    assertTrue(result1);

    // Decode second message
    boolean result2 = decoder.doDecode(mockSession, buffer, mockOutput);
    assertTrue(result2);

    // No more messages
    boolean result3 = decoder.doDecode(mockSession, buffer, mockOutput);
    assertFalse(result3);

    verify(mockOutput, times(2)).write(any());
  }

  @Test
  void testDecodeEmptyBuffer() throws Exception {
    IoBuffer buffer = IoBuffer.allocate(0);
    buffer.flip();

    boolean result = decoder.doDecode(mockSession, buffer, mockOutput);

    assertFalse(result);
    verify(mockOutput, never()).write(any());
  }

  private IoBuffer createEncodedBuffer(Message message) throws Exception {
    int messageSize = message.getSerializedSize();
    IoBuffer buffer = IoBuffer.allocate(4 + messageSize);
    CodedOutputStream cos = CodedOutputStream.newInstance(buffer.asOutputStream());
    cos.writeFixed32NoTag(messageSize);
    message.writeTo(cos);
    cos.flush();
    buffer.flip();
    return buffer;
  }
}
