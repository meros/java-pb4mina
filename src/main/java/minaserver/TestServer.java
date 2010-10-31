package minaserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import minaserver.TestProtFile.Person;
import minaserver.TestProtFile.Person.Builder;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.meros.pb4mina.ProtoBufCoderFilter;
import org.meros.pb4mina.ProtoBufMessageFactory;
import org.slf4j.LoggerFactory;


/**
 * Hello world!
 *
 */
public class TestServer 
{
	
    protected static final Object DECODER = new Object();
	private static org.slf4j.Logger logger;
	
	public static void main( String[] args ) throws IOException, InterruptedException
    {
	/*	Builder builder = Person.newBuilder();
		builder.setId(0);
		builder.setName("Alex Schrab");
		builder.setEmail("alexander.schrab@gmail.com");
		
		Message message = builder.build();
		
		int size = message.getSerializedSize();
		byte[] buf = new byte[4 + size];
		CodedOutputStream outputStream = CodedOutputStream.newInstance(buf);
		outputStream.writeFixed32NoTag(size);
		message.writeTo(outputStream);
		
		FileOutputStream file = new FileOutputStream(new File("/tmp/persondata"));
		file.write(buf);
		file.close();*/
		
		logger = LoggerFactory.getLogger(TestServer.class);
		
		NioSocketAcceptor socketAcceptor = new NioSocketAcceptor();
		socketAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		socketAcceptor.getFilterChain().addLast("codec", new ProtoBufCoderFilter(new ProtoBufMessageFactory() {
			
			@Override
			public Builder createProtoBufMessage() {
				return Person.newBuilder();
			}
		}));
		
		socketAcceptor.setHandler(new IoHandler() {
			
			@Override
			public void sessionOpened(IoSession arg0) throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void sessionCreated(IoSession session) throws Exception {

			}
			
			@Override
			public void sessionClosed(IoSession arg0) throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void messageSent(IoSession arg0, Object arg1) throws Exception {
			}
			
			@Override
			public void messageReceived(IoSession arg0, Object arg1) throws Exception {
				Person person = (Person) arg1;
				arg0.write(person.toBuilder().setName("reply").build());
			}
			
			@Override
			public void exceptionCaught(IoSession arg0, Throwable arg1)
					throws Exception {
				logger.debug("Caught an exception!");
				// TODO Auto-generated method stub
				
			}
		});
    
		socketAcceptor.bind(new InetSocketAddress(8083));	
		
		while (true) {
			Thread.sleep(100);
		}
    }
}
