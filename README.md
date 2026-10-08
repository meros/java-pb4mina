# pb4mina

[![Java CI](https://github.com/meros/java-pb4mina/actions/workflows/ci.yml/badge.svg)](https://github.com/meros/java-pb4mina/actions/workflows/ci.yml)
[![Java Version](https://img.shields.io/badge/java-11%2B-blue)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

Protocol Buffer encoder/decoder for [Apache MINA](https://mina.apache.org/) Java network application framework.

## Description

pb4mina connects Google Protocol Buffers to Apache MINA, so a MINA server can send and receive protobuf messages directly. It frames each message with a 4-byte length header, which makes it suitable for TCP-based communication.

I wrote it in 2010 as a proof of concept and updated it in 2025 to Java 11, current dependencies, tests and CI.

## Features

- **Protocol Buffer Integration**: Encode and decode Protocol Buffer messages over MINA sessions
- **Length-Prefixed Framing**: Messages are framed with a 4-byte fixed32 length header for reliable message boundaries
- **Session-Safe Decoder**: Stateful decoder maintains per-session state for handling partial messages
- **Shared Encoder**: Thread-safe encoder shared across all sessions for efficiency

## Requirements

- Java 11 or higher
- Apache Maven 3.6+

## Installation

pb4mina is not published to Maven Central. Build and install it into your local Maven repository first:

```bash
git clone https://github.com/meros/java-pb4mina.git
cd java-pb4mina
mvn install
```

Then add the dependency to your `pom.xml`:

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

Write Protocol Buffer messages to the session:

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

## Code Formatting

This project uses [Spotless](https://github.com/diffplug/spotless) with Google Java Format for code formatting.

```bash
# Check formatting
mvn spotless:check

# Apply formatting
mvn spotless:apply
```

## Dependencies

| Dependency | Version | Description |
|------------|---------|-------------|
| Apache MINA Core | 2.0.27 | Network application framework |
| Protocol Buffers | 3.25.5 | Serialization library |
| SLF4J | 2.0.16 | Logging facade |

## Status

This is a proof-of-concept project. It is not actively maintained, but pull requests are welcome.

## License

MIT. See [LICENSE](LICENSE).
