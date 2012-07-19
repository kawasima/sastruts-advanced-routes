package net.unit8.sastruts.routing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.unit8.sastruts.routing.options.ConditionOptions;
import net.unit8.sastruts.routing.options.RequirementOptions;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;

public class Route {
	private LinkedList<Segment> segments;

	public Route(LinkedList<Segment> segments, RequirementOptions requirements, ConditionOptions conditions) {
		this.segments = segments;
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
}
