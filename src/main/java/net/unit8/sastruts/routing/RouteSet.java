package net.unit8.sastruts.routing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
		int i = -1;
		for (Route route : routes) {
			i += 1;
			StringBuilder sb = new StringBuilder();
			for (Segment seg : route.getSegments()) {
				sb.append(seg.toString());
			}
			String[] segments = toPlainSegments(sb.toString());
			System.out.println(segments);
			for (String seg : segments) {
				/*
				if (StringUtil.isNotEmpty(seg) && seg.charAt(0) == ':') {
					seg = ":dynamic";
				}
				if (node.isEmpty() || ) {
					node.add();
				}
				*/
			}
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
}
