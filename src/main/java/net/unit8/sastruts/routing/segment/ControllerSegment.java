package net.unit8.sastruts.routing.segment;

import java.util.regex.Pattern;

import net.unit8.sastruts.routing.Options;

public class ControllerSegment extends DynamicSegment {
	public ControllerSegment(String value, Options options) {
		super(value, options);
	}

	public ControllerSegment(String key) {
		super(key);
	}

	@Override
	public Pattern regexpChunk() {
		return Pattern.compile("(?i-:(#{");
	}
}
