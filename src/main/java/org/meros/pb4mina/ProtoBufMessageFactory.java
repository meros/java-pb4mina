package org.meros.pb4mina;

import com.google.protobuf.Message.Builder;

/**
 * Interface for a factory creating protobuf builders for the protobuf decoder
 *
 * @author meros
 */
public interface ProtoBufMessageFactory {
  Builder createProtoBufMessage();
}
