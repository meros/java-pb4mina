package minaserver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import minaserver.TestProtFile.Person;
import minaserver.TestProtFile.Person.Builder;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.meros.pb4mina.ProtoBufCoderFilter;
import org.meros.pb4mina.ProtoBufMessageFactory;



public class TestClient {
	public static void main( String[] args )
	{
		NioSocketConnector socketConnector = new NioSocketConnector();

		socketConnector.getFilterChain().addLast("logger", new LoggingFilter());
		socketConnector.getFilterChain().addLast("protobufcoder", new ProtoBufCoderFilter(new ProtoBufMessageFactory() {
			@Override
			public Builder createProtoBufMessage() {
				return Person.newBuilder();
			}
		}));
		socketConnector.getFilterChain().addLast("logger2", new LoggingFilter());


		socketConnector.setHandler(new IoHandler() {

			@Override
			public void sessionOpened(IoSession session) throws Exception {
				Person person = Person.newBuilder().setId(0).setName("Alex").setEmail("test").build();
				session.write(person);
			}

			@Override
			public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
				// TODO Auto-generated method stub
			}

			@Override
			public void sessionCreated(IoSession session) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void sessionClosed(IoSession session) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void messageSent(IoSession session, Object message) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void messageReceived(IoSession session, Object message)
			throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
				// TODO Auto-generated method stub

			}
		});


		SocketAddress remoteAddress = new InetSocketAddress("localhost", 8083);


		ConnectFuture cf = socketConnector.connect(remoteAddress);

		// Wait for the connection attempt to be finished.
		cf.awaitUninterruptibly();
		cf.getSession().getCloseFuture().awaitUninterruptibly();

		socketConnector.dispose();
	}
}
