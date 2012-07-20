package net.unit8.sastruts.routing.segment;

import java.util.regex.Pattern;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Segment;

public class OptionalFormatSegment extends DynamicSegment {
	public OptionalFormatSegment(String key, Options options) {
		super(":format", options.$("optional", true));
	}

	public OptionalFormatSegment() {
		this(null, new Options());
	}

	@Override
	public String interpolationChunk() {
		return "." + super.interpolationChunk();
	}

	@Override
	public Pattern regexpChunk() {
		return Pattern.compile("/|(\\.[^/?\\.]+)?");
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
