package net.unit8.sastruts;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Routes;
import net.unit8.sastruts.testapp.action.UserAction;
import net.unit8.sastruts.testapp.action.admin.ProofAction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.util.S2ExecuteConfigUtil;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class UrlRewriterTest {

	@Test
	public void test() {
		Options options = UrlRewriter.parseOptionString("User#list?id=1");
		assertThat(options.getString("controller"), is("User"));
		assertThat(options.getString("action"), is("list"));
		assertThat(options.getString("id"), is("1"));
	}

	@Test
	public void testNoController() {
		S2ExecuteConfig executeConfig = new S2ExecuteConfig();
		executeConfig.setMethod(ReflectionUtil.getMethod(UserAction.class,
				"list"));
		S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
		Options options = UrlRewriter.parseOptionString("list?id=1");
		assertThat(options.getString("controller"), is("User"));
		assertThat(options.getString("action"), is("list"));
		assertThat(options.getString("id"), is("1"));
	}

	@Test
	public void testSubPackage() {
		S2ExecuteConfig executeConfig = new S2ExecuteConfig();
		executeConfig.setMethod(ReflectionUtil.getMethod(ProofAction.class,
				"index"));
		S2ExecuteConfigUtil.setExecuteConfig(executeConfig);
		Options options = UrlRewriter.parseOptionString("index?id=1");
		assertThat(options.getString("controller"), is("admin.Proof"));
		assertThat(options.getString("action"), is("index"));
		assertThat(options.getString("id"), is("1"));
	}

	@Test
	public void testGenerateOption() {
		Routes.load(ResourceUtil.getResourceAsFile("routes/testutil.xml"));
		assertThat(UrlRewriter.urlFor("my#account"), is("/my/account"));
		assertThat(UrlRewriter.urlFor("my#account?trailing_slash=true"),
				is("/my/account/"));
		assertThat(UrlRewriter.urlFor("my#account?trailing_slash=false"),
				is("/my/account"));
		assertThat(UrlRewriter.urlFor("my#account?anchor=p3"),
				is("/my/account#p3"));
	}

}
