package minaserver;

import org.apache.mina.filter.codec.ProtocolCodecFilter;

public class ProtoBufCoderFilter extends ProtocolCodecFilter {
	public ProtoBufCoderFilter(ProtoBufMessageFactory protoBufMessageFactory) {
		super(new ProtoBufCoderFactory(protoBufMessageFactory));
	}
}
