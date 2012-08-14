package net.unit8.sastruts.routing.recognizer;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Recognizer;
import net.unit8.sastruts.routing.Route;
import net.unit8.sastruts.routing.segment.RoutingException;

public class OptimizedRecognizer extends Recognizer {
	private SegmentNode tree;
	private List<Route> routes;

	private String[] toPlainSegments(String str) {
		str = str.replaceAll("^/+", "").replaceAll("/+$", "");
		return str.split("\\.[^/]+\\/+|/+|\\.[^/]+\\Z");
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
		tree = new SegmentNode(0);

		int i = -1;
		for (Route route : routes) {
			i += 1;
			SegmentNode node = tree;
			String[] segments = toPlainSegments(StringUtils.join(route.getSegments(), ""));
			for (String seg : segments) {
				if (StringUtil.isNotEmpty(seg) && seg.charAt(0) == '?') {
					seg = ":dynamic";
				}
				if (node.isEmpty() || !StringUtil.equals(node.lastChild().getLabel(), seg))
					node.add(new SegmentNode(seg, i));
				node = node.lastChild();
			}
		}
	}

	private int calcIndex(String[] segments, SegmentNode node, int level) {
		if (node.size() <= 1 || segments.length == level)
			return node.getIndex();
		String seg = segments[level];
		for (SegmentNode item : node.getChildNodes()) {
			if (StringUtil.equals(item.getLabel(), ":dynamic") || StringUtil.equals(item.getLabel(), seg)) {
				return calcIndex(segments, item, level + 1);
			}
		}
		return node.getIndex();
	}

	public Options recognize(String path) {
		String[] segments = toPlainSegments(path);
		int index = calcIndex(segments, tree, 0);
		while (index < routes.size()) {
			Options result = routes.get(index).recognize(path);
			if (result != null) return result;
			index += 1;
		}
		throw new RoutingException("No route matches " + path);
	}

	private class SegmentNode {
		private int index;
		private String label;
		private List<SegmentNode> childNodes;

		public SegmentNode(int index) {
			this(null, index);
		}

		public SegmentNode(String label, int index) {
			this.index = index;
			this.label = label;
			childNodes = CollectionsUtil.newArrayList();
		}

		public void add(SegmentNode child) {
			childNodes.add(child);
		}

		public boolean isEmpty() {
			return childNodes.isEmpty();
		}

		public SegmentNode lastChild() {
			if (isEmpty())
				return null;
			return childNodes.get(childNodes.size() - 1);
		}

		public String getLabel() {
			return label;
		}

		public int getIndex() {
			return index;
		}

		public List<SegmentNode> getChildNodes() {
			return childNodes;
		}
		public int size() {
			return childNodes.size();
		}
	}
}
