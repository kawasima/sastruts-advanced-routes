package net.unit8.sastruts.routing.segment;

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

	public int numberOfCaptures() {
		return 1;
	}

	public boolean optionalityImplied() {
		return true;
	}
}
