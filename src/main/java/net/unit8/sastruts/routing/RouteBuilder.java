package net.unit8.sastruts.routing;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.unit8.sastruts.routing.segment.ControllerSegment;
import net.unit8.sastruts.routing.segment.DividerSegment;
import net.unit8.sastruts.routing.segment.DynamicSegment;
import net.unit8.sastruts.routing.segment.OptionalFormatSegment;
import net.unit8.sastruts.routing.segment.PathSegment;
import net.unit8.sastruts.routing.segment.StaticSegment;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;

public class RouteBuilder {
	private static final String[] SEPARATORS = {"/", ".", "?"};
	private static final Pattern PTN_OPTIONAL_FORMAT = Pattern.compile("\\A\\.(:format?)\\/");
	private static final Pattern PTN_SYMBOL          = Pattern.compile("\\A:(\\w+)");
	private static final Pattern PTN_PATH            = Pattern.compile("\\A\\*(\\w+)");
	private static final Pattern PTN_STATIC          = Pattern.compile("\\A\\?(.*?)\\?");

	private String[] separators;
	private String[] optionalSeparators;
	private Pattern separatorRegexp;
	private Pattern nonseparatorRegexp;
	private Pattern intervalRegexp;

	public RouteBuilder() {
		separators = SEPARATORS;
		optionalSeparators = new String[]{"/"};
		separatorRegexp = Pattern.compile("[" + Pattern.quote(StringUtils.join(SEPARATORS)) + "]");
		nonseparatorRegexp = Pattern.compile("\\A([^" + Pattern.quote(StringUtils.join(SEPARATORS))+ "]+)");
		intervalRegexp = Pattern.compile("(.*?)([" + Pattern.quote(StringUtils.join(SEPARATORS)) + "]|$");
	}

	public LinkedList<Segment> segmentsForRoutePath(String path) {
		LinkedList<Segment> segments = new LinkedList<Segment>();
		StringBuilder rest = new StringBuilder(path);

		while (rest.length() > 0) {
			Segment segment = segmentFor(rest);
			segments.add(segment);
		}
		return segments;
	}

	public Segment segmentFor(StringBuilder sb) {
		String str = sb.toString();
		Segment segment = null;
		Matcher m;
		if ((m = PTN_OPTIONAL_FORMAT.matcher(str)).find()) {
			segment = new OptionalFormatSegment();
		} else if ((m = PTN_SYMBOL.matcher(str)).find()) {
			String key = m.group(1);
			segment = StringUtil.equals(key, ":controller") ? new ControllerSegment(key) : new DynamicSegment(key);
		} else if ((m = PTN_PATH.matcher(str)).find()) {
			segment = new PathSegment(m.group(1), true);
		} else if ((m = PTN_STATIC.matcher(str)).find()) {
			segment = new StaticSegment(m.group(1), true);
		} else if ((m = nonseparatorRegexp.matcher(str)).find()) {
			segment = new StaticSegment(m.group(1));
		} else if ((m = separatorRegexp.matcher(str)).find()) {
			segment = new DividerSegment();
		}
		return segment;
	}

	public void divideRouteOptions(LinkedList<Segment> segments, RouteOptions options) {
		options.except("pathPrefix", "namePrefix");

		if (options.hasNamespace()) {
			String namespace = options.getNamespace().replace("/$", "");
			options.setController(namespace + "/" + options.getController());
		}


	}


	public Route build(String path, RouteOptions options) {
		if (path.charAt(0) != '/')
			path = "/" + path;

		if (path.charAt(path.length() - 1) != '/')
			path = path + "/";

		String prefix = options.getPathPrefix().replace("^/", "");
		if (StringUtil.isNotBlank(prefix))
			path = "/" + prefix + path;

		LinkedList<Segment> segments = segmentsForRoutePath(path);
		divideRouteOptions(segments, options);

		Route route = new Route(segments, null/*requirements*/, null/*conditions*/);
		return route;
	}
}
