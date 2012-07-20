package net.unit8.sastruts.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

public class Route {
	private LinkedList<Segment> segments;
	private Options requirements;
	private Options conditions;
	private List<String> significantKeys;

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
}
