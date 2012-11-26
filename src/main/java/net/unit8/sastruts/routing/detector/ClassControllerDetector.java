package net.unit8.sastruts.routing.detector;

import java.util.ArrayList;
import java.util.List;

import net.unit8.sastruts.ARStringUtil;
import net.unit8.sastruts.ControllerUtil;
import net.unit8.sastruts.routing.ControllerDetector;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.util.ClassTraversal.ClassHandler;
import org.seasar.framework.util.ResourcesUtil;
import org.seasar.framework.util.ResourcesUtil.Resources;
import org.seasar.framework.util.StringUtil;

/**
 * HotDeploy/WarmDeploy時用のControllerDetectorです。
 *
 * @author kawasima
 * @author growthfield
 *
 */
public class ClassControllerDetector implements ControllerDetector {
	/**
	 * @see ControllerDetector#detect()
	 */
	public List<String> detect() {
		final List<String> controllers = new ArrayList<String>();
		S2Container container = SingletonS2ContainerFactory.getContainer();
		NamingConvention namingConvention = (NamingConvention)container.getComponent(NamingConvention.class);
		final String actionSuffix = namingConvention.getActionSuffix();
		final String actionSubPackageName = namingConvention.fromSuffixToPackageName(actionSuffix);
		for (String rootPackageName : namingConvention.getRootPackageNames()) {
			final String actionPackageName = rootPackageName.concat(".").concat(actionSubPackageName);
			ClassHandler handler = new ClassHandler() {
				public void processClass(String packageName, String shortClassName) {
					String pkgPath = ARStringUtil.removeStart(packageName, actionPackageName);
					String uncapitalizedShortClassName = ControllerUtil.fromClassNameToPath(shortClassName);
					String actionPath = StringUtil.trimSuffix(uncapitalizedShortClassName, actionSuffix);
					StringBuilder sb = new StringBuilder();
					if (StringUtil.isNotEmpty(pkgPath)) {
						sb.append(pkgPath.substring(1)).append("/");
					}
					sb.append(actionPath);
					controllers.add(sb.toString());
				}
			};
			Resources[] types = ResourcesUtil.getResourcesTypes(actionPackageName);
			for (Resources r : types) {
				r.forEach(handler);
			}
		}
		return controllers;
	}
}
