package org.meros.pb4mina;

import java.io.IOException;
import java.io.InputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.Message;

/**
 * 
 * Decoder for protobuf messages. The messages are delimited by a 4 byte uint32 size header 
 * 
 * @author meros
 *
 */
public class ProtoBufDecoder extends CumulativeProtocolDecoder {

	private final ProtoBufMessageFactory protoBufMessageFactory;

	enum State {
		ReadingLength,
		ReadingPackage,
	}
	
	State state = State.ReadingLength;
	
	static final int packageLengthTokenSize = 4;
	int packageLength = 0;

	private Logger logger;
	
	ProtoBufDecoder(ProtoBufMessageFactory protoBufMessageFactory) {
		logger = LoggerFactory.getLogger(ProtoBufDecoder.class);
		this.protoBufMessageFactory = protoBufMessageFactory;		
	}
	
	@Override
	protected boolean doDecode(
			IoSession ioSession, 
			IoBuffer ioBuffer,
			ProtocolDecoderOutput decoderOutput) {
		try {

			if (state == State.ReadingLength) {
				packageLength = readLength(ioBuffer);

				if (packageLength != -1) {
					state = State.ReadingPackage;
				}				
			}

			if (state == State.ReadingPackage) {
				Message message = readMessage(ioBuffer, protoBufMessageFactory, packageLength);

				if (message != null) {
					decoderOutput.write(message);
					
					state = State.ReadingLength;

					//There might be more message to be parsed 
					return true;
				}					
			}
		} catch(IOException e) {
			logger.error("IOException while trying to decode a readmessage", e);
			ioSession.close(true);
		}

		//Need more data
		return false;
	}

	private static Message readMessage(
			IoBuffer inputStream,
			ProtoBufMessageFactory protoBufMessageFactory,
			int packageLength) throws IOException {
		int remainingBytesInStream = inputStream.remaining(); 
		if (remainingBytesInStream < packageLength) {
			//Not enough data to parse message
			return null;
		} else {
			//Create a delimited input stream around the real input stream
			InputStream delimitedInputStream = new BoundedInputStream(inputStream.asInputStream(), packageLength);
			
			//Retrieve a builder
			com.google.protobuf.GeneratedMessage.Builder<?> builder = protoBufMessageFactory.createProtoBufMessage();
		
			//And parse/build the message
			builder.mergeFrom(delimitedInputStream);
			Message message = builder.build();
			
			return message;
		}
	}

	private static int readLength(IoBuffer inputStream) throws IOException {
		if (inputStream.remaining() < packageLengthTokenSize) {
			return -1;
		} 
		
		//Create lenght delimited input stream
		InputStream delimitedInputStream = new BoundedInputStream(inputStream.asInputStream(), packageLengthTokenSize);

		//Read length of incoming protobuf
		CodedInputStream codedInputStream = CodedInputStream.newInstance(delimitedInputStream);
		return codedInputStream.readFixed32();
	}

}
