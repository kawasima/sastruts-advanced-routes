package net.unit8.sastruts.routing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RouteSet {
	private List<File> configurationFiles;
	private List<Route> routes;
	private RouteBuilder builder;

	public RouteSet() {
		routes = new ArrayList<Route>();
	}

	public void recognizedPath(String path) {

	}

	public void segmentTree(List<Route> routes) {
		int i = -1;
		for (Route route : routes) {
			i += 1;
			StringBuilder sb = new StringBuilder();
			for (Segment seg : route.getSegments()) {
				sb.append(seg.toString());
			}
			String[][] node;
			String[] segments = toPlainSegments(sb.toString());
			for (String seg : segments) {
			}
		}
	}

	public String[] toPlainSegments(String segs) {
		segs = segs.replaceFirst("^\\/+", "").replaceFirst("\\/+$", "");
		String[] segments = segs.split("\\.[^\\/]+\\/+|\\/+|\\.[^\\/]+\\Z");
		return segments;
	}
}
