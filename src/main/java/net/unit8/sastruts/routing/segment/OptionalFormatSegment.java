package net.unit8.sastruts.routing.segment;

import net.unit8.sastruts.routing.Options;

public class OptionalFormatSegment extends DynamicSegment {
	public OptionalFormatSegment(String key, Options options) {
		super(":format", options.$("optional", true));
	}

	public OptionalFormatSegment() {
		this(null, new Options());
	}

	@Override
	public String interpolationChunk(Options hash) {
		return "." + super.interpolationChunk(hash);
	}

	@Override
	public String regexpChunk() {
		return "/|(\\.[^/?\\.]+)?";
	}

	@Override
	public String toString() {
		return "(.:format)?";
	}

	public String extractValue() {
		return localName() + " = options.getString('" + getKey() + "').toLowerCase()";
	}

	public void matchExtraction() {

	}
}
