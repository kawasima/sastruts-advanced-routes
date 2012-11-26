package net.unit8.sastruts.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.unit8.sastruts.routing.segment.RoutingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.mock.servlet.MockHttpServletRequest;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.struts.util.RequestUtil;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class ViaTest {
	@Test
	public void testGet() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/via.xml"));
		System.out.println(Routes.getRouteSet().toString());
		MockHttpServletRequest request = ((MockHttpServletRequest) RequestUtil
				.getRequest());
		request.setMethod("GET");
		Options options = Routes.recognizePath("/methods/");
		System.out.println(options);

		assertThat(options.getString("action"), is("getAndPost"));
	}

	@Test
	public void testPost() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/via.xml"));
		System.out.println(Routes.getRouteSet().toString());
		MockHttpServletRequest request = ((MockHttpServletRequest) RequestUtil
				.getRequest());
		request.setMethod("POST");
		Options options = Routes.recognizePath("/methods/");
		System.out.println(options);

		assertThat(options.getString("action"), is("getAndPost"));
	}

	@Test(expected = RoutingException.class)
	public void testPut() throws RoutingException {
		Routes.load(ResourceUtil.getResourceAsFile("routes/via.xml"));
		System.out.println(Routes.getRouteSet().toString());
		MockHttpServletRequest request = ((MockHttpServletRequest) RequestUtil
				.getRequest());
		request.setMethod("PUT");
		Routes.recognizePath("/methods/");
	}
}
