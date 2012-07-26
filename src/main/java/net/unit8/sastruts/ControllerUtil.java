package net.unit8.sastruts;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.StringUtil;

public class ControllerUtil {

	public static String fromPathToClassName(String path) {
		String className = StringUtil.replace(path, "/", ".");
		if (className.lastIndexOf('.') >= 0) {
			className = StringUtils.substringBeforeLast(className, ".") + "." + StringUtils.capitalize(StringUtils.substringAfterLast(className, "."));
		} else {
			className = StringUtils.capitalize(className);
		}
		return className;
	}

	public static String fromClassNameToPath(String className) {
		String path = StringUtil.replace(className, ".", "/");
		if (path.lastIndexOf('/') >= 0) {
			path = StringUtils.substringBeforeLast(path, "/") + "/" + StringUtils.uncapitalize(StringUtils.substringAfterLast(path, "/"));
		} else {
			path = StringUtils.uncapitalize(path);
		}
		return path;
	}

}
