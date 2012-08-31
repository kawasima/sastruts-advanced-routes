package net.unit8.sastruts.routing;

import static net.unit8.sastruts.routing.RoutingTestUtil.assertRecognizes;

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
		assertRecognizes("ns1.Blog#show?blogCd=01", "/ns1/blog/01");
		assertRecognizes("Blog#show?blogCd=01", "/ns2/blog/01");
		assertRecognizes("ns3.Blog#show?blogCd=01", "/blog/01");
	}

}
