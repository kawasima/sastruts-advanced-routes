package net.unit8.sastruts.routing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class DefaultTest {
	@Test
	public void test() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/default.xml"));
		RoutingTestUtil.assertRecognizes("Photos#show?format=jpg&id=100",
				"/photos");
		RoutingTestUtil.assertRecognizes("Photos#show?format=jpg&id=5",
				"/photos/5");
		RoutingTestUtil.assertRecognizes("Photos#show?format=png&id=5",
				"/photos/5.png");
	}
}
