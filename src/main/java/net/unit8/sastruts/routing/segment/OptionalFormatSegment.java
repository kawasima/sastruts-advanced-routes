package net.unit8.sastruts.routing.segment;

import java.util.regex.Pattern;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Segment;

public class OptionalFormatSegment extends DynamicSegment {
	public OptionalFormatSegment(String key, Options options) {
		super(key, options);
	}
	
	@Override
	public String interpolationChunk() {
		return "." + super.interpolationChunk();
	}
	
	@Override
	public Pattern regexpChunk() {
		return Pattern.compile("");
	}
}
