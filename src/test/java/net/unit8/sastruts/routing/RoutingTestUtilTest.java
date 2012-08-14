package net.unit8.sastruts.routing;

import static net.unit8.sastruts.routing.RoutingTestUtil.assertGenerates;
import static net.unit8.sastruts.routing.RoutingTestUtil.assertRecognizes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.mock.servlet.MockHttpServletRequest;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.struts.util.RequestUtil;


@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class RoutingTestUtilTest {
	private static final int ITER = 100;
	@Test
	public void test() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/testutil.xml"));

		assertRecognizes("Previews#news", "/news/preview");
		long t1 = System.currentTimeMillis();
		for (int i=0; i<ITER; i++) {
			((MockHttpServletRequest)RequestUtil.getRequest()).setMethod("GET");
			assertRecognizes("Search#index?id=8", "/projects/8/search");
		}
		long t2 = System.currentTimeMillis();
		System.out.println("recognize " + ITER + "times: " + (t2 - t1) + "ms");

		assertGenerates("/uploads", "Attachments#upload");
		assertGenerates("/projects/5/issues/calendar", "Calendars#show?projectId=5");
		assertGenerates("/issues/calendar", "Calendars#show");
	}
}
