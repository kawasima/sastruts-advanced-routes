package net.unit8.sastruts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Routes;

import org.seasar.framework.container.ComponentNotFoundRuntimeException;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.util.StringConversionUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.URLEncoderUtil;

public class UrlRewriter {
	public static String contextPath = null;

	public static String urlFor(Map<String, Object> opts) {
		Options options = new Options(opts);
		return urlFor(options);
	}

	public static String urlFor(String optionString) {
		Options options = parseOptionString(optionString);
		return urlFor(options);
	}

	public static String urlFor(Options options) {
		StringBuilder url = new StringBuilder();

		boolean trailingSlash = false;
		if (options.containsKey("trailing_slash")) {
			trailingSlash = options.getBoolean("trailing_slash");
			options.remove("trailing_slash");
		}

		String anchor = null;
		if (options.containsKey("anchor")) {
			anchor = "#" + URLEncoderUtil.encode(options.getString("anchor"));
			options.remove("anchor");
		}
		if (StringUtil.isNotEmpty(contextPath))
			url.append(contextPath);
		String generated = Routes.generate(options);
		String path = trailingSlash ? trailingSlash(generated) : generated;
		try {
			HttpServletResponse response = ResponseUtil.getResponse();
			HttpSession session = RequestUtil.getRequest().getSession(false);
			if (session != null)
				path = response.encodeURL(path);
		} catch (ComponentNotFoundRuntimeException e) {
			// If not use in webapp, ignore the process of encode url.
		}
		url.append(path);
		if (StringUtil.isNotEmpty(anchor))
			url.append(anchor);
		return url.toString();
	}

	private static String trailingSlash(String url) {
		int queryStringIdx = url.indexOf('?');
		if (queryStringIdx < 0 || url.length() < 1)
			return url + "/";
		if (queryStringIdx != 0 && url.charAt(queryStringIdx - 1) == '/') {
			return url;
		} else {
			return url.substring(0, queryStringIdx) + "/" + url.substring(queryStringIdx);
		}
	}

	@SuppressWarnings("unchecked")
	public static Options parseOptionString(String optionString) {
		String[] urlTokens = ARStringUtil.split(optionString, "?", 2);
		String[] actionTokens = ARStringUtil.split(urlTokens[0], "#", 2);

		Options options = new Options();
		if (actionTokens.length == 1) {
			options.$("action", actionTokens[0]);
			options.$("controller", currentController());
		} else {
			options.$("controller", actionTokens[0]).$("action",
					actionTokens[1]);
		}
		if (urlTokens.length == 2 && StringUtil.isNotEmpty(urlTokens[1])) {
			String[] paramToken = StringUtil.split(urlTokens[1], "&");
			for (String keyValuePair : paramToken) {
				String[] pair = ARStringUtil.split(keyValuePair, "=", 2);
				if (pair.length == 1) {
					options.$(pair[0], null);
				} else if (pair.length == 2) {
					Object value = options.get(pair[0]);
					if (value == null) {
						options.$(pair[0], pair[1]);
					} else if (value instanceof ArrayList) {
						((ArrayList<String>) value).add(pair[1]);
					} else {
						List<String> values = new ArrayList<String>();
						values.add(StringConversionUtil.toString(value));
						options.$(pair[0], values);
					}
				}
			}
		}
		return options;
	}

	public static String currentController() {
		S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.getExecuteConfig();
		Class<?> clazz = executeConfig.getMethod().getDeclaringClass();
		NamingConvention namingConvention = SingletonS2Container
				.getComponent(NamingConvention.class);
		String componentName = namingConvention
				.fromClassNameToComponentName(clazz.getName());
		return StringUtil.trimSuffix(namingConvention
				.fromComponentNameToPartOfClassName(componentName),
				namingConvention.getActionSuffix());
	}
}
