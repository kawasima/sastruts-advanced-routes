package net.unit8.sastruts;

import java.util.Map;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Routes;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.util.S2ExecuteConfigUtil;

public class UrlRewriter {
	public static String urlFor(Map<String, Object> opts) {
		Options options = new Options(opts);
		StringBuilder url = new StringBuilder();
		url.append(Routes.generate(options));
		return url.toString();
	}

	public static String urlFor(String optionString) {
		Options options = parseOptionString(optionString);
		StringBuilder url = new StringBuilder();
		url.append(Routes.generate(options));
		return url.toString();
	}

	protected static Options parseOptionString(String optionString) {
		String[] urlTokens    = StringUtils.split(optionString, "?", 2);
		String[] actionTokens = StringUtils.split(urlTokens[0], "#", 2);

		Options options = new Options();
		if (actionTokens.length == 1) {
			options.$("action", actionTokens[0]);
			options.$("controller", currentController());
		} else {
			options.$("controller", actionTokens[0]).$("action", actionTokens[1]);
		}
		if (StringUtil.isNotEmpty(urlTokens[1])) {
			String[] paramToken = StringUtils.split(urlTokens[1], "=", 2);
			if (paramToken.length == 1) {
				options.$(paramToken[0], null);
			} else if (paramToken.length == 2) {
				options.$(paramToken[0], paramToken[1]);
			}
		}
		return options;
	}

	public static String currentController() {
		S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.getExecuteConfig();
		Class<?> clazz = executeConfig.getMethod().getDeclaringClass();
		NamingConvention namingConvention = SingletonS2Container.getComponent(NamingConvention.class);
		String componentName = namingConvention.fromClassNameToComponentName(clazz.getName());
		return StringUtil.trimSuffix(namingConvention.fromComponentNameToPartOfClassName(componentName), namingConvention.getActionSuffix());
	}
}
