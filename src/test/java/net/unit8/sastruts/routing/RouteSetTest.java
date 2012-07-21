package net.unit8.sastruts.routing;

import org.junit.Test;

public class RouteSetTest {
	@Test
	public void test() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/post/", new Options().$("controller", "post"));
		routeSet.addRoute("/post/:id", new Options().$("controller", "post").$("action", "show"));
		Route route = routeSet.addRoute("/post/:id/comments", new Options().$("controller", "post").$("action", "comments"));
		System.out.println(route);
		Options params = route.recognize("/post/1/comments");
		System.out.println(params);
	}
}
