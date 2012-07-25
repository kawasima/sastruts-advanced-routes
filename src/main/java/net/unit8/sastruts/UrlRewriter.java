package net.unit8.sastruts;

import java.util.Map;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Routes;

import org.apache.commons.lang.StringUtils;

public class UrlRewriter {
	public static String urlFor(Map<String, Object> opts) {
		Options options = new Options(opts);
		StringBuilder url = new StringBuilder();
		url.append(Routes.generate(options));
		return url.toString();
	}

	public static String urlFor(String optionString) {
		String[] urlTokens    = StringUtils.split(optionString, "?", 2);
		String[] actionTokens = StringUtils.split(urlTokens[0], "#", 2);

		Options options = new Options();
		if (actionTokens.length == 1) {
			options.$("action", actionTokens[0]);
			options.$("controller", currentController());
		}
		StringBuilder url = new StringBuilder();
		url.append(Routes.generate(options));
		return url.toString();
	}

	public static String currentController() {
		return null;
	}
}
