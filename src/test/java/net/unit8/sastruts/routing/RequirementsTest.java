package net.unit8.sastruts.routing;

import static net.unit8.sastruts.routing.RoutingTestUtil.assertGenerates;
import static net.unit8.sastruts.routing.RoutingTestUtil.assertRecognizes;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.unit8.sastruts.routing.segment.RoutingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class RequirementsTest {
	@Test
	public void test() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/requirements.xml"));
		System.out.println(Routes.getRouteSet().toString());
		Options options = Routes.recognizePath("/posts/2012/11");
		System.out.println(options);

		assertThat(options.getString("controller"), is("Posts"));
		assertThat(options.getString("action"), is("index"));
		assertThat(options.getString("year"), is("2012"));
		assertThat(options.getString("month"), is("11"));
	}

	@Test(expected = RoutingException.class)
	public void testNotMatch() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/requirements.xml"));
		Routes.recognizePath("/posts/20XX/11"); // throw RoutingException
	}

	@Test
	public void testParentheses() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/requirements.xml"));
		assertRecognizes("Images#show?width=640&height=480", "/images/640x480");
		assertGenerates("/images/640x480", "Images#show?width=640&height=480");
		assertRecognizes("Sample#detail?sampleCd=001", "/sample_001/detail");
		assertRecognizes("Images#size?size=1024", "/images/1024");
		assertRecognizes("Images#size?size=sacd", "/images/sacd");
	}

	@Test
	public void generate() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/requirements.xml"));
		String path = Routes.generate(new Options().$("controller", "Posts")
				.$("action", "index").$("year", "2012").$("month", "11"));
		assertThat(path, is("/posts/2012/11"));
	}

	@Test
	public void generateSameAction() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/requirements.xml"));
		assertGenerates("/area_01/zip_03", "sample.Hoge#index?area=01&zip=03");
		assertGenerates("/area_01/addr_04", "sample.Hoge#index?area=01&addr=04");
		assertGenerates("/area_01/addr_04?zip=03",
				"sample.Hoge#index?area=01&addr=04&zip=03");
	}
}
