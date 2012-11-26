package net.unit8.sastruts.routing.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import net.unit8.sastruts.ARStringUtil;
import net.unit8.sastruts.ControllerUtil;
import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.RegexpUtil;
import net.unit8.sastruts.routing.Routes;

import org.seasar.framework.util.StringUtil;

public class ControllerSegment extends DynamicSegment {
	public ControllerSegment(String value, Options options) {
		super(value, options);
	}

	public ControllerSegment(String key) {
		super(key);
	}

	@Override
	public String regexpChunk() {
		List<String> possibleNames = new ArrayList<String>();
		for (String name : Routes.possibleControllers()) {
			possibleNames.add(RegexpUtil.escape(name));
		}
		return "(?i-:(" + ARStringUtil.join(possibleNames, "|")+ "))";
	}

	@Override
	public void matchExtraction(Options params, Matcher match, int nextCapture) {
		String key = getKey();
		String token = match.group(nextCapture);
		if (getDefault() != null) {
			params.put(key, StringUtil.isNotEmpty(token) ? ControllerUtil.fromPathToClassName(token) : getDefault());
		} else {
			 if (StringUtil.isNotEmpty(token))
				 params.put(key, ControllerUtil.fromPathToClassName(token));
		}
	}

	@Override
	public String interpolationChunk(Options hash) {
		String value = hash.getString(getKey());
		String path = StringUtil.replace(value, ".", "/");
		if (path != null) {
			if (path.lastIndexOf('/') >= 0)
				path = ARStringUtil.substringBeforeLast(path, "/") + "/" + StringUtil.decapitalize(ARStringUtil.substringAfterLast(path, "/"));
			else
				path = StringUtil.decapitalize(path);
		}
		return path;
	}
}
