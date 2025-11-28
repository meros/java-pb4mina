package org.meros.pb4mina;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.protobuf.Any;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ProtoBufCoderFactory} */
class ProtoBufCoderFactoryTest {

  private ProtoBufCoderFactory factory;
  private ProtoBufMessageFactory messageFactory;

  @BeforeEach
  void setUp() {
    messageFactory = () -> Any.newBuilder();
    factory = new ProtoBufCoderFactory(messageFactory);
  }

  @Test
  void testGetEncoderReturnsSameInstance() throws Exception {
    IoSession session1 = mock(IoSession.class);
    IoSession session2 = mock(IoSession.class);

    ProtocolEncoder encoder1 = factory.getEncoder(session1);
    ProtocolEncoder encoder2 = factory.getEncoder(session2);

    assertNotNull(encoder1);
    assertNotNull(encoder2);
    assertSame(encoder1, encoder2, "Encoder should be shared across sessions");
    assertTrue(encoder1 instanceof ProtoBufEncoder);
  }

  @Test
  void testGetDecoderCreatesSeparateInstancePerSession() throws Exception {
    IoSession session1 = mock(IoSession.class);
    IoSession session2 = mock(IoSession.class);

    // First session should create a new decoder
    when(session1.getAttribute(any())).thenReturn(null);
    ProtocolDecoder decoder1 = factory.getDecoder(session1);

    // Second session should also create a new decoder
    when(session2.getAttribute(any())).thenReturn(null);
    ProtocolDecoder decoder2 = factory.getDecoder(session2);

    assertNotNull(decoder1);
    assertNotNull(decoder2);
    assertTrue(decoder1 instanceof ProtoBufDecoder);
    assertTrue(decoder2 instanceof ProtoBufDecoder);

    // Verify setAttribute was called to store the decoder
    verify(session1).setAttribute(any(), eq(decoder1));
    verify(session2).setAttribute(any(), eq(decoder2));
  }

  @Test
  void testGetDecoderReusesExistingDecoder() throws Exception {
    IoSession session = mock(IoSession.class);
    ProtoBufDecoder existingDecoder = new ProtoBufDecoder(messageFactory);

    // Session already has a decoder
    when(session.getAttribute(any())).thenReturn(existingDecoder);

    ProtocolDecoder decoder = factory.getDecoder(session);

    assertSame(existingDecoder, decoder);
    // setAttribute should not be called since decoder already exists
    verify(session, never()).setAttribute(any(), any());
  }
}
