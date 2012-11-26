package net.unit8.sastruts.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class PathMatchingTest {
	@Test
	public void pathBasic() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/paths.xml"));
		System.out.println(Routes.getRouteSet().toString());
		Options options = Routes.recognizePath("/photos/12");
		System.out.println(options);
		options = Routes.recognizePath("/photos/long/path/to/12");
		assertThat(options.getString("other"), is("long/path/to/12"));
	}

	@After
	public void tearDown() {
		Routes.getRouteSet().clear();
	}

	@Test
	public void pathMiddle() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/paths.xml"));
		System.out.println(Routes.getRouteSet().toString());
		Options options = Routes
				.recognizePath("/books/some/section/last-words-a-memoir");
		System.out.println(options);
		assertThat(options.getString("section"), is("some/section"));
		assertThat(options.getString("title"), is("last-words-a-memoir"));
	}

}
