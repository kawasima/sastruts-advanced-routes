package net.unit8.sastruts.routing;

public class Routes {
	private static RouteSet routeSet;

	public static String generate(Options options) {
		routeSet = getRouteSet();
		return routeSet.generate(options);
	}

	private static synchronized RouteSet getRouteSet() {
		if (routeSet == null)
			routeSet = new RouteSet();
		return routeSet;
	}
}
