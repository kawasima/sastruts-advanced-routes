package net.unit8.sastruts.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;

public class Route {
	private LinkedList<Segment> segments;
	private Options requirements;
	private Options conditions;
	private List<String> significantKeys;
	private Options parameterShell;
	private boolean matchingPrepared;
	private String controllerRequirement;
	private String actionRequirement;

	public Route(LinkedList<Segment> segments, Options requirements, Options conditions) {
		this.segments = segments;
		this.requirements = requirements;
		this.conditions = conditions;
	}

	public LinkedList<Segment> getSegments() {
		return segments;
	}

	public String buildQueryString(Map<String, String> hash) {
		List<String> elements = new ArrayList<String>();
		for(String key : hash.keySet()) {
			String value = hash.get(key);
			if (StringUtil.isNotEmpty(value)) {
				elements.add(key + "=" + value);
			}
		}
		return StringUtils.join(elements, "&");
	}

	public List<String> significantKeys() {
		if (significantKeys != null)
			return significantKeys;
		Set<String> sk = new HashSet<String>();
		for (Segment segment : segments) {
			if (segment.hasKey()) {
				sk.add(segment.getKey());
			}
		}
		sk.addAll(requirements.keySet());
		significantKeys = new ArrayList<String>(sk);
		return significantKeys;
	}

	@Override
	public String toString() {
		StringBuilder segs = new StringBuilder();
		for (Segment s : segments) {
			segs.append(s.toString());
		}
		String method = conditions.getString("method");
		if (StringUtil.isEmpty(method))
			method = "any";

		return String.format("%-6s %-40s %s", method.toUpperCase(), segs.toString(), requirements);
	}

	/*----recognize----*/
	public Options recognize(String path) {
		Pattern pattern = Pattern.compile(recognitionPattern(true));
		Matcher match = pattern.matcher(path);
		Options params = null;
		if (match.find()) {
			int nextCapture = 1;
			params = getParameterShell();
			for (Segment segment : segments) {
				segment.matchExtraction(params, match, nextCapture);
				nextCapture += segment.numberOfCaptures();
			}
		}
		return params;
	}

	private String recognitionPattern(boolean wrap) {
		String pattern = "";
		for (int i = segments.size() - 1; i >= 0; i--) {
			Segment segment = segments.get(i);
			pattern = segment.buildPattern(pattern);
		}
		return wrap ? ("\\A" + pattern + "\\Z") : pattern;
	}

	private Options getParameterShell() {
		if (parameterShell == null) {
			parameterShell = new Options();
			for (Map.Entry<String, Object> e : requirements.entrySet()) {
				if (! (e.getValue() instanceof Pattern)) {
					parameterShell.put(e.getKey(), e.getValue());
				}
			}
		}
		return parameterShell;

	}

	/* --- generate --- */
	public boolean matchesControllerAndAction(String controller, String action) {
		prepareMatching();
		return  (controllerRequirement == null || StringUtil.equals(controllerRequirement, controller)) &&
				(actionRequirement == null || StringUtil.equals(actionRequirement, action));
	}

	public String generate(Options options, Options hash) {
		if (generationRequirements()) {
			for (Segment segment : segments) {
				if (segment.hasKey()) {

				}
			}
		}
		return null;
	}

	public boolean generationRequirements() {
		for(String key : requirements.keySet()) {

		}
		return false;
	}
	private String requirementFor(String key) {
		if (requirements.containsKey(key))
			return requirements.getString(key);
		for (Segment segment : segments) {
			if (segment.hasKey() && StringUtil.equals(segment.getKey(), key)) {
				return segment.getRegexp();
			}
		}
		return null;
	}
	private void prepareMatching() {
		if (!matchingPrepared) {
			controllerRequirement = requirementFor("controller");
			actionRequirement = requirementFor("action");
			matchingPrepared = true;
		}
	}
}
