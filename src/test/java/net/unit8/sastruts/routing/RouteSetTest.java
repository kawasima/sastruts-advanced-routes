package net.unit8.sastruts.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Route;
import net.unit8.sastruts.routing.RouteSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class RouteSetTest {
	@Test
	public void recognize() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/post/", new Options().$("controller", "post"));
		routeSet.addRoute("/post/:id", new Options().$("controller", "post").$("action", "show"));
		Route route = routeSet.addRoute("/post/:id/comments", new Options().$("controller", "post").$("action", "comments"));
		System.out.println(route);
		Options params = routeSet.recognizePath("/post/1/comments");
		assertThat(params.getString("controller"), is("post"));
		System.out.println(params);
	}

	@Test
	public void recognizeControllerWithSubPackage() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/post/", new Options().$("controller", "user.Post"));
		routeSet.addRoute("/post/:id", new Options().$("controller", "user.Post").$("action", "show"));
		Route route = routeSet.addRoute("/post/:id/comments", new Options().$("controller", "user.Post").$("action", "comments"));
		System.out.println(route);
		Options params = route.recognize("/post/1/comments");
		assertThat(params.getString("controller"), is("user.Post"));
		System.out.println(params);
	}

	@Test
	public void recognizeIndex() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/post/", new Options().$("controller", "user.Post"));
		routeSet.addRoute("/post/:id", new Options().$("controller", "user.Post").$("action", "show"));
		routeSet.addRoute("/post/:id/comments", new Options().$("controller", "user.Post").$("action", "comments"));
		Options params = routeSet.recognizePath("/");
		System.out.println(params);

	}

	@Test
	public void recognizeController() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/:controller/:action", new Options());
		Options params = routeSet.recognizePath("/user/list");
		assertThat(params.getString("controller"), is("User"));
		assertThat(params.getString("action"), is("list"));

		params = routeSet.recognizePath("/admin/proof");
		assertThat(params.getString("controller"), is("admin.Proof"));
		assertThat(params.getString("action"), is("index"));
	}
}
