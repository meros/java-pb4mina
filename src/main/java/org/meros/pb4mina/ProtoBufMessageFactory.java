package org.meros.pb4mina;

import minaserver.TestProtFile.Person.Builder;

/**
 * Interface for a factory creating protobuf builders for the protobuf decoder 
 * 
 * @author meros
 *
 */
public interface ProtoBufMessageFactory {
	Builder createProtoBufMessage();
}
