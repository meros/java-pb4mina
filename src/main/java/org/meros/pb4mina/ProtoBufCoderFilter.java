package org.meros.pb4mina;

import org.apache.mina.filter.codec.ProtocolCodecFilter;

/**
 * Helper class to make it easier to create a protobuf filter
 *
 * @author meros
 */
public class ProtoBufCoderFilter extends ProtocolCodecFilter {
  public ProtoBufCoderFilter(ProtoBufMessageFactory protoBufMessageFactory) {
    super(new ProtoBufCoderFactory(protoBufMessageFactory));
  }
}
