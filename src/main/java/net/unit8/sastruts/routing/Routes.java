package net.unit8.sastruts.routing;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.unit8.sastruts.routing.detector.ClassControllerDetector;
import net.unit8.sastruts.routing.detector.ComponentControllerDetector;

import org.seasar.framework.container.hotdeploy.HotdeployBehavior;
import org.seasar.framework.container.impl.S2ContainerBehavior;
import org.seasar.framework.container.impl.S2ContainerBehavior.Provider;
import org.seasar.framework.container.warmdeploy.WarmdeployBehavior;

public class Routes {
	private static volatile List<String> possibleControllers = null;
	private static RouteSet routeSet = new RouteSet();

	public static final List<String> HTTP_METHODS = Collections.unmodifiableList(
			Arrays.asList(new String[]{"GET" , "HEAD", "POST", "PUT", "DELETE", "OPTIONS"}));


	public static String generate(Options options) {
		return getRouteSet().generate(options);
	}

	public static Options recognizePath(String path) {
		return getRouteSet().recognizePath(path);
	}

	public static void load(InputStream stream) {
		getRouteSet().loadStream(stream);
	}

	public static void load(File config) {
		getRouteSet().getConfigurationFiles().clear();
		getRouteSet().addConfigurationFile(config);
		getRouteSet().load();
	}

	public static synchronized RouteSet getRouteSet() {
		return routeSet;
	}

	public static synchronized List<String> possibleControllers() {
		if (possibleControllers == null) {
			synchronized(Routes.class) {
				if (possibleControllers == null) {
					Provider provider = S2ContainerBehavior.getProvider();
					ControllerDetector detector = (provider instanceof WarmdeployBehavior || provider instanceof HotdeployBehavior) ?
							new ClassControllerDetector() : new ComponentControllerDetector();
					possibleControllers = detector.detect();
				}
			}
		}
		return possibleControllers;
	}

}
