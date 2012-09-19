package net.unit8.sastruts.routing;

import java.util.Arrays;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Route;
import net.unit8.sastruts.routing.RouteSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;

import antlr.collections.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class RouteGenerationTest {
	@Test
	public void test() {
		RouteSet routeSet = new RouteSet();
		Route route = routeSet.addRoute("/blog/:id/comments", new Options().$("controller", "Blog").$("action", "comments"));
		Options hash = new Options().$("controller", "Blog").$("action", "comments").$("id", 8).$("other", "parameter");
		Options options = new Options().$("controller", "Blog").$("action", "comments").$("id", 8).$("other", "parameter");
		String url = route.generate(options, hash);
		assertThat(url, is("/blog/8/comments?other=parameter"));
	}

	@Test
	public void testMultipleParameter() {
		RouteSet routeSet = new RouteSet();
		Route route = routeSet.addRoute("/blog/:id/comments", new Options().$("controller", "Blog").$("action", "comments"));
		Options hash = new Options().$("controller", "Blog").$("action", "comments").$("id", 8).$("other",Arrays.asList("parameter", "parameter2"));
		Options options = new Options().$("controller", "Blog").$("action", "comments").$("id", 8).$("other", Arrays.asList("parameter", "parameter2"));
		String url = route.generate(options, hash);
		assertThat(url, is("/blog/8/comments?other=parameter&other=parameter2"));
	}


	@Test
	public void testController() {
		RouteSet routeSet = new RouteSet();
		Route route = routeSet.addRoute("/:controller/:action", new Options());
		String url = route.generate(new Options(), new Options().$("controller", "Blog").$("action", "comments").$("id", 8));
		assertThat(url, is("/blog/comments"));
	}
}
