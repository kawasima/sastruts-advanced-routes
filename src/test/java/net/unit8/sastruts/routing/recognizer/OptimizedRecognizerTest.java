package net.unit8.sastruts.routing.recognizer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import net.unit8.sastruts.routing.Routes;
import net.unit8.sastruts.routing.RoutingTestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class OptimizedRecognizerTest {

	@Test
	public void test() {
		OptimizedRecognizer recognizer = new OptimizedRecognizer();
		String[] segments = recognizer
				.toPlainSegments("/images/(:width)x(:height)");
		assertThat(segments.length, is(4));
		assertThat(segments[1], is(":width"));
	}

    @Test
    public void testDynamicSegments() {
        Routes.load(ResourceUtil.getResourceAsFile("routes/recognizer.xml"));
        RoutingTestUtil.assertGenerates("/hello/world", "DynamicGreet#index?greeting=hello&name=world");
        RoutingTestUtil.assertRecognizes("DynamicGreet#index?greeting=hello&name=world", "/hello/world");
    }
}
