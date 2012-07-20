package net.unit8.sastruts.routing.segment;

import java.util.regex.Pattern;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Segment;

public class StaticSegment extends Segment {
	private String value;
	private boolean raw;
	private boolean isOptional;

	public StaticSegment(String value) {
		this(value, null);
	}
	public StaticSegment(String value, Options options) {
		super();
		this.value = value;
		if (options.containsKey("raw"))
			this.raw = options.getBoolean("raw"); 
		if (options.containsKey("optional"))
			this.isOptional = options.getBoolean("optional"); 
	}

	public StaticSegment(String value, boolean optional) {
		super();
		this.value = value;
	}

	@Override
	public String interpolationChunk() {
		return raw ? value : super.interpolationChunk();
	}

	public Pattern regexpChunk() {
		Pattern chunk = Pattern.compile(Pattern.quote(value));
		return isOptional ? chunk : chunk; // TODO
	}

	public int getNumberOfCaptures() {
		return 0;
	}

	public void buildPattern(String pattern) {
		
	}
	@Override
	public String toString() {
		return value;
	}
}
