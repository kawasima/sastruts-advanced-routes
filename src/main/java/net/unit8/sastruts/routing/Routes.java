package net.unit8.sastruts.routing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.unit8.sastruts.ControllerUtil;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.util.StringUtil;

public class Routes {
	private static List<String> possibleControllers = null;
	private static RouteSet routeSet;
	private static NamingConvention namingConvention;

	public static String generate(Options options) {
		return getRouteSet().generate(options);
	}

	public static Options recognizePath(String path) {
		return getRouteSet().recognizePath(path);
	}

	public static void load(File config) {
		getRouteSet().addConfigurationFile(config);
		getRouteSet().load();
	}
	private static synchronized RouteSet getRouteSet() {
		if (routeSet == null)
			routeSet = new RouteSet();
		return routeSet;
	}

	public static synchronized List<String> possibleControllers() {
		S2Container container = SingletonS2ContainerFactory.getContainer();
		namingConvention = (NamingConvention)container.getComponent(NamingConvention.class);
		if (possibleControllers == null) {
			possibleControllers = new ArrayList<String>();
			collectAction(container);
		}
		return possibleControllers;
	}

	private static void collectAction(S2Container container) {
		for (int i=0; i<container.getComponentDefSize(); i++) {
			ComponentDef componentDef = container.getComponentDef(i);
			if (componentDef.getComponentName() == null)
				continue;
			String className = namingConvention.fromComponentNameToPartOfClassName(componentDef.getComponentName());
			if (!className.endsWith(namingConvention.getActionSuffix()))
				continue;
			className = StringUtil.trimSuffix(className, namingConvention.getActionSuffix());
			String path = ControllerUtil.fromClassNameToPath(className);
			possibleControllers.add(path);
		}
		for (int i=0; i<container.getChildSize(); i++) {
			collectAction(container.getChild(i));
		}

	}
}
