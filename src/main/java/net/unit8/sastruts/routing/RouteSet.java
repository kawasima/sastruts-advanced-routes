package net.unit8.sastruts.routing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.unit8.sastruts.routing.segment.RoutingException;

import org.seasar.framework.util.StringUtil;

public class RouteSet {
	private List<File> configurationFiles;
	private List<Route> routes;
	private RouteBuilder builder;

	public RouteSet() {
		routes = new ArrayList<Route>();
	}

	public void recognizedPath(String path) {

	}

	public void recognizeOptimized(String path) {
	}

	public void segmentTree() {
		SegmentNode root = new SegmentNode(null);

		for (Route route : routes) {
			StringBuilder sb = new StringBuilder();
			for (Segment seg : route.getSegments()) {
				sb.append(seg.toString());
			}
			String[] segments = toPlainSegments(sb.toString());
			
			SegmentNode node = root;
			for (String seg : segments) {
				if (StringUtil.isNotEmpty(seg) && seg.charAt(0) == ':') {
					seg = ":dynamic";
				}
				if (node.children.containsKey(seg)) {
					node = node.children.get(seg);
				} else {
				}
			}
		}
	}
	
	static class SegmentNode {
		private String name;
		private int index;
		private HashMap<String, SegmentNode> children;
		
		SegmentNode(Segment segment) {
			//this.segment = segment;
		}
	}
/*
	static class RouteNode {
		public RouteNode(Segment)
		public Segment segment;
		public List<Integer>
	}
*/
	public String[] toPlainSegments(String segs) {
		segs = segs.replaceFirst("^\\/+", "").replaceFirst("\\/+$", "");
		String[] segments = segs.split("\\.[^\\/]+\\/+|\\/+|\\.[^\\/]+\\Z");
		return segments;
	}

	public RouteBuilder getBuilder() {
		if (builder == null)
			builder = new RouteBuilder();

		return builder;
	}

	public Route addRoute(String path, Options options) {
		Route route = getBuilder().build(path, options);
		routes.add(route);
		return route;
	}
	
	public Options recognizePath(String path) {
		for (Route route : routes) {
			Options result = route.recognize(path);
			if (result != null) return result;
		}
		throw new RoutingException("No route matches " + path);
	}
}
