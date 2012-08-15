package net.unit8.sastruts.routing.segment;

import java.util.regex.Matcher;

import org.seasar.framework.util.URLUtil;

import net.unit8.sastruts.routing.Options;

public class OptionalFormatSegment extends DynamicSegment {
	public OptionalFormatSegment(String key, Options options) {
		super("format", options.$("optional", true));
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

	@Override
	public void matchExtraction(Options params, Matcher match, int nextCapture) {
		String m = match.group(nextCapture);
		if (m != null) {
			params.put(getKey(), URLUtil.decode(m.substring(1), "UTF-8"));
		} else {
			params.put(getKey(), URLUtil.decode(getDefault(), "UTF-8"));
		}
	}
}
