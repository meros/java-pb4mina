package minaserver;

import java.io.InvalidObjectException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;

public class ProtoBufEncoder extends ProtocolEncoderAdapter {

	static final int packageLengthTokenSize = 4;
	
	@Override
	public void encode(
			IoSession session, 
			Object message,
			ProtocolEncoderOutput out) throws Exception {
		if (!(message instanceof Message)) {
			throw new InvalidObjectException("You need to provide a protocol buffer message to the protocol buffer encoder");
		}
		
		Message protoMessage = (Message) message;
		
		//Get size of message
		int messageSize = protoMessage.getSerializedSize();
		IoBuffer buffer = IoBuffer.allocate(messageSize + packageLengthTokenSize);
		CodedOutputStream outputStream = CodedOutputStream.newInstance(buffer.asOutputStream());
		
		//Write length delimited
		outputStream.writeFixed32NoTag(messageSize);
		protoMessage.writeTo(outputStream);
		outputStream.flush();
	
		//Flip the buffer to be able to start reading from the buffer
		buffer.flip();
		
		//Pass on the serialized data
		out.write(buffer);
	}

}
