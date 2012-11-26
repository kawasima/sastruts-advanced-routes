package net.unit8.sastruts;

import org.seasar.framework.util.StringUtil;

public class ControllerUtil {

	public static String fromPathToClassName(String path) {
		String className = StringUtil.replace(path, "/", ".");
		if (className.lastIndexOf('.') >= 0) {
			className = ARStringUtil.substringBeforeLast(className, ".") + "." + StringUtil.capitalize(ARStringUtil.substringAfterLast(className, "."));
		} else {
			className = StringUtil.capitalize(className);
		}
		return className;
	}

	public static String fromClassNameToPath(String className) {
		String path = StringUtil.replace(className, ".", "/");
		if (path.lastIndexOf('/') >= 0) {
			path = ARStringUtil.substringBeforeLast(path, "/") + "/" + StringUtil.decapitalize(ARStringUtil.substringAfterLast(path, "/"));
		} else {
			path = StringUtil.decapitalize(path);
		}
		return path;
	}

}
