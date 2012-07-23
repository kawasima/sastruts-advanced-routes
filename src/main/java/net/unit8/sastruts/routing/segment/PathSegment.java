package net.unit8.sastruts.routing.segment;

import java.util.regex.Matcher;

import org.seasar.framework.util.StringUtil;

import net.unit8.sastruts.routing.Options;

public class PathSegment extends DynamicSegment {
	public PathSegment(String key, Options options) {
		super(key, options);
	}

	@Override
	public String interpolationChunk(Options hash) {
		String value = hash.getString(getKey());
		return value;
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
