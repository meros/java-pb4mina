package org.meros.pb4mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Factory for the protobuf coder classes
 *
 * @author meros
 */
public class ProtoBufCoderFactory implements ProtocolCodecFactory {

  private static ProtocolEncoder staticEncoder = new ProtoBufEncoder();
  private static final Object DECODER = new Object();
  private ProtoBufMessageFactory protoBufMessageFactory;

  /**
   * @param protoBufMessageFactory factory that creates builders that the decoder uses to parse
   *     incoming messages
   */
  public ProtoBufCoderFactory(ProtoBufMessageFactory protoBufMessageFactory) {
    this.protoBufMessageFactory = protoBufMessageFactory;
  }

  @Override
  public ProtocolDecoder getDecoder(IoSession session) throws Exception {
    // The decoder is stateful, hence we need one/session
    Object decoder = session.getAttribute(DECODER);

    // No decoder created for this session
    if (decoder == null) {
      decoder = new ProtoBufDecoder(protoBufMessageFactory);
      session.setAttribute(DECODER, decoder);
    }

    return (ProtocolDecoder) decoder;
  }

  @Override
  public ProtocolEncoder getEncoder(IoSession session) throws Exception {
    return staticEncoder;
  }
}
