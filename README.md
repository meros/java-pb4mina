# pb4mina

[![Java CI](https://github.com/meros/java-pb4mina/actions/workflows/ci.yml/badge.svg)](https://github.com/meros/java-pb4mina/actions/workflows/ci.yml)
[![Java Version](https://img.shields.io/badge/java-11%2B-blue)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

Protocol Buffer encoder/decoder for [Apache MINA](https://mina.apache.org/) Java network application framework.

## Description

pb4mina provides a seamless integration between Google Protocol Buffers and Apache MINA, enabling efficient binary message serialization for network applications. It handles message framing using a 4-byte fixed-length header, making it suitable for TCP-based communication.

## Features

- **Protocol Buffer Integration**: Encode and decode Protocol Buffer messages over MINA sessions
- **Length-Prefixed Framing**: Messages are framed with a 4-byte fixed32 length header for reliable message boundaries
- **Session-Safe Decoder**: Stateful decoder maintains per-session state for handling partial messages
- **Shared Encoder**: Thread-safe encoder shared across all sessions for efficiency

## Requirements

- Java 11 or higher
- Apache Maven 3.6+

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.meros</groupId>
    <artifactId>pb4mina</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

### Basic Setup

1. Create a message factory that returns builders for your Protocol Buffer messages:

```java
import com.google.protobuf.Message.Builder;
import org.meros.pb4mina.ProtoBufMessageFactory;

public class MyMessageFactory implements ProtoBufMessageFactory {
    @Override
    public Builder createProtoBufMessage() {
        return MyProtoBufMessage.newBuilder();
    }
}
```

2. Add the codec filter to your MINA filter chain:

```java
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.meros.pb4mina.ProtoBufCoderFilter;

DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
filterChain.addLast("codec", new ProtoBufCoderFilter(new MyMessageFactory()));
```

3. Handle messages in your IoHandler:

```java
@Override
public void messageReceived(IoSession session, Object message) {
    MyProtoBufMessage protoMessage = (MyProtoBufMessage) message;
    // Process the message
}

@Override
public void messageSent(IoSession session, Object message) {
    // Message was sent successfully
}
```

### Sending Messages

Simply write Protocol Buffer messages to the session:

```java
MyProtoBufMessage message = MyProtoBufMessage.newBuilder()
    .setField("value")
    .build();
session.write(message);
```

## Wire Format

Messages are transmitted using the following format:

```
+----------------+------------------+
| Length (4 bytes) | Protobuf Data  |
+----------------+------------------+
```

- **Length**: 4-byte fixed32 (little-endian) containing the size of the protobuf data
- **Protobuf Data**: The serialized Protocol Buffer message

## Building from Source

```bash
# Clone the repository
git clone https://github.com/meros/java-pb4mina.git
cd java-pb4mina

# Build and run tests
mvn clean verify

# Install to local repository
mvn install
```

## Dependencies

| Dependency | Version | Description |
|------------|---------|-------------|
| Apache MINA Core | 2.0.27 | Network application framework |
| Protocol Buffers | 3.25.5 | Serialization library |
| SLF4J | 2.0.16 | Logging facade |

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source. See the repository for license details.

## Status

This is a proof-of-concept project. It is not actively maintained but contributions are welcome.
