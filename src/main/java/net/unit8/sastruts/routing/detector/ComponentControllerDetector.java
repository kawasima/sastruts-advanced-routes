package net.unit8.sastruts.routing.detector;

import java.util.ArrayList;
import java.util.List;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.util.StringUtil;

import net.unit8.sastruts.ControllerUtil;
import net.unit8.sastruts.routing.ControllerDetector;

public class ComponentControllerDetector implements ControllerDetector {
	private NamingConvention namingConvention;
	public ComponentControllerDetector() {
		S2Container container = SingletonS2ContainerFactory.getContainer();
		namingConvention = (NamingConvention)container.getComponent(NamingConvention.class);
	}

	public List<String> detect() {
		List<String> controllers = new ArrayList<String>();
		S2Container container = SingletonS2ContainerFactory.getContainer();
		traverse(controllers, container);
		return controllers;
	}

	private void traverse(List<String> controllers, S2Container container) {
		for (int i=0; i<container.getComponentDefSize(); i++) {
			ComponentDef componentDef = container.getComponentDef(i);
			if (componentDef.getComponentName() == null)
				continue;
			String className = namingConvention.fromComponentNameToPartOfClassName(componentDef.getComponentName());
			if (!className.endsWith(namingConvention.getActionSuffix()))
				continue;
			className = StringUtil.trimSuffix(className, namingConvention.getActionSuffix());
			String path = ControllerUtil.fromClassNameToPath(className);
			controllers.add(path);
		}
		for (int i=0; i<container.getChildSize(); i++) {
			traverse(controllers, container.getChild(i));
		}

	}

}
