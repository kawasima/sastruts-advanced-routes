package net.unit8.sastruts.routing.segment;

import java.util.regex.Pattern;

import org.seasar.framework.util.StringUtil;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.RegexpUtil;
import net.unit8.sastruts.routing.Segment;

public class StaticSegment extends Segment {
	private String value;
	private boolean raw;

	public StaticSegment(String value) {
		this(value, new Options());
	}
	public StaticSegment(String value, Options options) {
		super();
		this.value = value;
		if (options.containsKey("raw"))
			this.raw = options.getBoolean("raw");
		if (options.containsKey("optional"))
			setOptional(options.getBoolean("optional"));
	}

	@Override
	public String interpolationChunk() {
		return raw ? value : super.interpolationChunk();
	}

	@Override
	public Pattern regexpChunk() {
		Pattern chunk = Pattern.compile(Pattern.quote(value));
		return isOptional() ? chunk : chunk; // TODO
	}

	@Override
	public int numberOfCaptures() {
		return 0;
	}

	public String buildPattern(String pattern) {
		String escaped = Pattern.quote(value);
		if (isOptional() && StringUtil.isNotEmpty(pattern)) {
			return "?:" + RegexpUtil.optionalize(escaped) + "\\Z|" + escaped + RegexpUtil.unoptionalize(pattern);
		} else if (isOptional()) {
			return RegexpUtil.optionalize(escaped);
		} else {
			return escaped + pattern;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
