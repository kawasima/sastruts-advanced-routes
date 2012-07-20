package net.unit8.sastruts.routing.segment;

import java.util.regex.Pattern;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Segment;

public class DynamicSegment extends Segment {
	private String key;
	public DynamicSegment(String key, Options options) {
		this.key = key;
	}
	@Override
	public boolean hasKey() {
		return true;
	}
	
	@Override
	public String getKey() {
		return key;
	}
	
	public Pattern regexpChunk() {
		return Pattern.compile("");
	}
}
