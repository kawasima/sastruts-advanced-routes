package net.unit8.sastruts.routing;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Route;
import net.unit8.sastruts.routing.RouteSet;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class RouteGenerationTest {
	@Test
	public void test() {
		RouteSet routeSet = new RouteSet();
		Route route = routeSet.addRoute("/blog/:id/comments", new Options().$("controller", "blog").$("action", "comments"));
		String url = route.generate(new Options(), new Options().$("controller", "blog").$("action", "comments").$("id", 8));
		assertThat(url, is("/blog/8/comments"));
	}
}
