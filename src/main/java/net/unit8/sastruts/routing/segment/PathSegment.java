package net.unit8.sastruts.routing.segment;

import java.util.regex.Matcher;

import org.seasar.framework.util.StringUtil;

import net.unit8.sastruts.routing.Options;

public class PathSegment extends DynamicSegment {
	public PathSegment(String key, Options options) {
		super(key, options);
	}

	public String interpoationChunk() {
		return interpolationChunk(null);
	}

	public String interpolationChunk(String valueCode) {
		if (valueCode == null)
			valueCode = localName();

		return valueCode;
	}

	public String getDefault() {
		return "";
	}

	public void setDefault(String path) {
		if (StringUtil.isNotBlank(path))
			throw new RoutingException("paths cannot have non-empty default values");
	}

	public String defaultRegexpChunk() {
		return "(.*)";
	}

	@Override
	public int numberOfCaptures() {
		return 1;
	}

	public boolean optionalityImplied() {
		return true;
	}
	
	@Override
	public void matchExtraction(Options params, Matcher match, int nextCapture) {
		String value = match.group(nextCapture);
		params.put(getKey(), value);
	}
}
