package net.unit8.sastruts.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.RequestUtil;

public class Route {
	private List<Segment> segments;
	private Options requirements;
	private Options conditions;
	private List<String> significantKeys;
	private Options parameterShell;
	private boolean matchingPrepared;
	private String controllerRequirement;
	private String actionRequirement;
	private Pattern recognizePattern;

	public Route(List<Segment> segments, Options requirements, Options conditions) {
		this.segments = segments;
		this.requirements = requirements;
		this.conditions = conditions;

		if (!significantKeys().contains("action") && !requirements.containsKey("action")) {
			requirements.$("action", "index");
			significantKeys().add("action");
		}
	}

	public List<Segment> getSegments() {
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
		List<Object> methods = conditions.getList("method");
		if (methods.isEmpty()) {
			methods.add("any");
		}
		StringBuilder out = new StringBuilder(256);
		for (Object method : methods) {
			out.append(String.format("%-6s %-40s %s\n", method.toString().toUpperCase(), segs.toString(), requirements));
		}
		return out.toString();
	}

	/*----recognize----*/
	public Options recognize(String path) {
		HttpServletRequest request = RequestUtil.getRequest();
		List<Object> methods = conditions.getList("method");
		if (request != null && !methods.isEmpty() && !methods.contains(request.getMethod())) {
			return null;
		}
		if (recognizePattern == null) {
			recognizePattern = Pattern.compile(recognitionPattern(true));
		}
		Matcher match = recognizePattern.matcher(path);
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

	private String buildQueryString(Options hash, List<String> onlyKeys) {
		List<String> elements = new ArrayList<String>(hash.size());

		if (onlyKeys == null)
			onlyKeys = new ArrayList<String>(hash.keySet());

		for (String key : onlyKeys) {
			if (hash.containsKey(key)) {
				elements.add(hash.getUrlEncodedString(key));
			}
		}
		return elements.isEmpty() ? "" : "?" + StringUtils.join(elements, "&");
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
		String path = null;
		if (generationRequirements(options, hash)) {
			path = segments.get(segments.size() - 1).stringStructure(segments.subList(0, segments.size() - 1), hash);
		}
		return appendQueryString(path, hash, extraKeys(options));
	}

	public boolean generationRequirements(Options options, Options hash) {
		boolean matched = true;
		for(String key : requirements.keySet()) {
			Object req = requirements.get(key);
			if (req instanceof Pattern) {
				matched &= (hash.containsKey(key) && ((Pattern)req).matcher(options.getString(key)).matches());
			} else {
				matched &= StringUtil.equals(hash.getString(key), requirements.getString(key));
			}
		}
		return matched;
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

	private String appendQueryString(String path, Options hash, List<String> queryKeys) {
		if (path == null)
			return null;

		if (queryKeys == null)
			queryKeys = extraKeys(hash);

		return path + buildQueryString(hash, queryKeys);
	}

	private List<String> extraKeys(Options hash) {
		List<String> extraKeys = new ArrayList<String>();
		if (hash != null) {
			for (String key : hash.keySet()) {
				if (!significantKeys.contains(key))
					extraKeys.add(key);
			}
		}
		return extraKeys;
	}
}
