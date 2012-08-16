package net.unit8.sastruts.routing.segment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.RegexpUtil;
import net.unit8.sastruts.routing.RouteBuilder;
import net.unit8.sastruts.routing.Segment;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.URLUtil;

public class DynamicSegment extends Segment {
	private String key;
	private String defaultValue;
	private Pattern regexp;
	private boolean wrapParentheses = false;

	public DynamicSegment(String key) {
		this(key, new Options());
	}
	public DynamicSegment(String key, Options options) {
		this.key = key;
		if (options.containsKey("default"))
			this.defaultValue = options.getString("default");
		if (options.containsKey("regexp"))
			this.regexp = Pattern.compile(options.getString("regexp"));
		if (options.containsKey("wrapParentheses"))
			this.wrapParentheses = options.getBoolean("wrapParentheses");
	}

	@Override
	public String toString() {
		return wrapParentheses ? "(:" + key +")" : ":" + key;
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
		return regexp != null ? "(" + regexp.pattern() + ")" : defaultRegexpChunk();
	}

    public String defaultRegexpChunk() {
    	return "([^" + RegexpUtil.escape(StringUtils.join(RouteBuilder.SEPARATORS)) + "]+)";
    }

    @Override
    public boolean hasDefault() {
    	return true;
    }
    @Override
	public String getDefault() {
		return defaultValue;
	}

	@Override
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public void setRegexp(Pattern regexp) {
		this.regexp = regexp;
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

	@Override
	public String stringStructure(List<Segment> list, Options hash) {
		if (isOptional()) {
			if (StringUtils.equals(hash.getString(getKey()), getDefault())) {
				return continueStringStructure(list, hash);
			} else {
				return interpolationStatement(list, hash);
			}
		} else {
			return interpolationStatement(list, hash);
		}

	}
}
