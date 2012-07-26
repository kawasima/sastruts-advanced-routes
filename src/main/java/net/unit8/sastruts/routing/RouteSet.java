package net.unit8.sastruts.routing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unit8.sastruts.routing.segment.RoutingException;

import org.seasar.framework.util.StringUtil;

public class RouteSet {
	private List<File> configurationFiles;
	private List<Route> routes;
	private RouteBuilder builder;
	private RouteLoader loader;
	private Map<String, Map<String, List<Route>>> routesByController;

	public RouteSet() {
		routes = new ArrayList<Route>();
		configurationFiles = new ArrayList<File>();
		routesByController = new HashMap<String, Map<String,List<Route>>>();
		loader = new RouteLoader(this);
	}

	public void clear() {
		routes.clear();
	}

	public void addConfigurationFile(File file) {
		configurationFiles.add(file);
	}

	public void load() {
		clear();
		loadRoutes();
	}

	private void loadRoutes() {
		if (!configurationFiles.isEmpty()) {
			for (File config : configurationFiles) {
				loader.load(config);
			}
		} else {
			addRoute(":conroller/:action/:id", new Options());
		}
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

	public String generate(Options options) {
		Options merged = new Options(options);
		String controller = merged.getString("controller");
		String action     = merged.getString("action");
		if (StringUtil.isEmpty(controller) || StringUtil.isEmpty(action)) {
			throw new RoutingException("Need controller and action!");
		}
		List<Route> routes = routesByController(controller, action);
		for(Route route : routes) {
			String results = route.generate(options, merged);
			if (results != null) {
				return results;
			}
		}
		throw new RoutingException("No route matches " + options.toString() /*TODO Added pretty-print for Options#toString*/);
	}

	private List<Route> routesByController(String controller, String action) {
		Map<String, List<Route>> actionMap = routesByController.get(controller);
		if (actionMap == null) {
			actionMap = new HashMap<String, List<Route>>();
			routesByController.put(controller, actionMap);
		}
		List<Route> routesByAction = actionMap.get(action);
		if (routesByAction == null) {
			routesByAction = new ArrayList<Route>();
			for (Route route : routes) {
				if (route.matchesControllerAndAction(controller, action)) {
					routesByAction.add(route);
				}
			}
			actionMap.put(action, routesByAction);
		}
		return routesByAction;
	}
}