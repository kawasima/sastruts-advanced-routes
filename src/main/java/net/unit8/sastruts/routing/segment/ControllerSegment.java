package net.unit8.sastruts.routing.segment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.framework.util.StringUtil;

import net.unit8.sastruts.routing.Options;

public class ControllerSegment extends DynamicSegment {
	public ControllerSegment(String value, Options options) {
		super(value, options);
	}

	public ControllerSegment(String key) {
		super(key);
	}

	@Override
	public String regexpChunk() {
		return "(?i-:(#{";
	}
	
	@Override
	public void matchExtraction(Options params, Matcher match, int nextCapture) {
		String key = getKey();
		String token = match.group(nextCapture);
		if (getDefault() != null) {
			params.put(key, StringUtil.isNotEmpty(token) ? token.toLowerCase() : getDefault());
		} else {
			 if (StringUtil.isNotEmpty(token))
				 params.put(key, token.toLowerCase());
	}	

	}
}
