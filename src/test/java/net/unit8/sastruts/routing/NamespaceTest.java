package net.unit8.sastruts.routing;

import static net.unit8.sastruts.routing.RoutingTestUtil.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class NamespaceTest {
	@Test
	public void test() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/namespace.xml"));
		System.out.println(Routes.getRouteSet().toString());
		assertRecognizes("ns1.ns2.Blog#show?blogCd=01", "/ns1/ns2/blog/01");
		assertRecognizes("Blog#show?blogCd=01", "/ns2/blog/01");
		assertRecognizes("ns3.Blog#show?blogCd=01", "/blog/01");
	}

	@Test
	public void testNest() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/namespace.xml"));
		assertRecognizes("ns4.ns5.Blog2#show?blogCd=01", "/ns4/ns5/ns6/blog2/01");
	}

	@Test
	public void testRoot() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/namespace.xml"));
		assertGenerates("/", "Index#index");
	}
}
