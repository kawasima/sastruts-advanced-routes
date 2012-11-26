package net.unit8.sastruts;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.mock.servlet.MockHttpServletRequest;
import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockServletContext;
import org.seasar.framework.mock.servlet.MockServletContextImpl;
import org.seasar.framework.unit.Seasar2;

@RunWith(Seasar2.class)
public class AdvancedRoutingFilterTest {
	@Test
	public void testGetOriginalPath() {
		AdvancedRoutingFilter filter = new AdvancedRoutingFilter();
		MockServletContext context = new MockServletContextImpl("/");
		MockHttpServletRequest request = new MockHttpServletRequestImpl(
				context, "/hoge;jsession_id=huge?query=123");
		String path = filter.getOriginalPath(request);
		assertThat(path, is("/hoge"));

		filter.requestUriHeader = "X-Request-Uri";
		request.addHeader(filter.requestUriHeader,
				"/fuga;jsession_id=aisdhfasud?a=1&a=2");
		path = filter.getOriginalPath(request);
		assertThat(path, is("/fuga"));
	}
}
