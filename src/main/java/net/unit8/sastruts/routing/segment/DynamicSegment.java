package net.unit8.sastruts.routing.segment;

import java.util.regex.Matcher;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.RegexpUtil;
import net.unit8.sastruts.routing.RouteBuilder;
import net.unit8.sastruts.routing.Segment;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.URLUtil;

public class DynamicSegment extends Segment {
	private String key;
	private String defaultValue;
	private String regexp;

	public DynamicSegment(String key) {
		this(key, new Options());
	}
	public DynamicSegment(String key, Options options) {
		this.key = key;
		if(options.containsKey("default"))
			this.defaultValue = options.getString("default");
		if(options.containsKey("regexp"))
			this.regexp = options.getString("regexp");



	}

	@Override
	public String toString() {
		return ":" + key;
	}

	public String localName() {
		return key + "_value";
	}

	public String extractValue() {
		return "localName =";
	}

	@Override
	public boolean hasKey() {
		return true;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String regexpChunk() {
		return StringUtils.isNotEmpty(regexp) ? regexp : defaultRegexpChunk();
	}

    public String defaultRegexpChunk() {
    	return "([^" + StringUtils.join(RouteBuilder.SEPARATORS) + "]+)";
    }

	public String getDefault() {
		return defaultValue;
	}

	@Override
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public void matchExtraction(Options params, Matcher match, int nextCapture) {
		String m = match.group(nextCapture);
		String value = null;
		if (m != null) {
			value = URLUtil.decode(m, "UTF-8");
		} else {
			value = defaultValue;
		}
		params.put(key, value);
	}

	@Override
	public String buildPattern(String pattern) {
		pattern = regexpChunk() + pattern;
		return isOptional() ? RegexpUtil.optionalize(pattern) : pattern;
	}

	@Override
	public String interpolationChunk(Options hash) {
		String value = hash.getString(getKey());
		return URLUtil.encode(value, "UTF-8");
	}
}
