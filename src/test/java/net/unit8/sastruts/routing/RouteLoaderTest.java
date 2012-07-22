package net.unit8.sastruts.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;

import net.unit8.sastruts.routing.segment.RoutingException;

import org.junit.Test;
import org.seasar.framework.util.ResourceUtil;

public class RouteLoaderTest {

	@Test
	public void test() {
		File routes = ResourceUtil.getResourceAsFile("routes.xml");
		RouteSet routeSet = new RouteSet();
		routeSet.addConfigurationFile(routes);
		routeSet.load();
		Options options = routeSet.recognizePath("/user/list");
		assertThat(options.getString("controller"), is("admin.User"));
		try {
			routeSet.recognizePath("/user/unknown");
			fail("Didn't raise RoutingException");
		} catch (RoutingException e) {
			assertTrue("No route matches", true);
		}
		assertThat(options.getString("controller"), is("admin.User"));
	}
}
