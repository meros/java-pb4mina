package minaserver;

import minaserver.Test.Person.Builder;

//Factory creating protobuf builders for the protobuf decoder 
public interface ProtoBufMessageFactory {
	Builder createProtoBufMessage();
}
