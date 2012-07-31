package net.unit8.sastruts.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.RouteSet;
import net.unit8.sastruts.routing.segment.RoutingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class RouteLoaderTest {
	@Test
	public void test() {
		File routes = ResourceUtil.getResourceAsFile("routes.xml");
		RouteSet routeSet = new RouteSet();
		routeSet.addConfigurationFile(routes);
		routeSet.load();
		System.out.println(routeSet);
		Options options = routeSet.recognizePath("/user/list");
		assertThat(options.getString("controller"), is("admin.User"));
		try {
			Options params = routeSet.recognizePath("/post/unknown");
			System.out.println(params);
			fail("Didn't raise RoutingException");
		} catch (RoutingException e) {
			assertTrue("No route matches", true);
		}
		assertThat(options.getString("controller"), is("admin.User"));
	}
}
