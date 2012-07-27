package net.unit8.sastruts.routing.detector;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.unit8.sastruts.ControllerUtil;
import net.unit8.sastruts.routing.ControllerDetector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.StringUtil;

public class ClassControllerDetector implements ControllerDetector {
	private static final String[] EXTENSIONS = {"class"};
	public List<String> detect() {
		List<String> controllers = new ArrayList<String>();

		S2Container container = SingletonS2ContainerFactory.getContainer();
		NamingConvention namingConvention = (NamingConvention)container.getComponent(NamingConvention.class);

		String actionSuffix = namingConvention.getActionSuffix();
		String actionSubPackageName = namingConvention.fromSuffixToPackageName(actionSuffix);
		for (String rootPackageName : namingConvention.getRootPackageNames()) {
			String actionClasspath = StringUtil.replace(rootPackageName, ".", "/").concat("/").concat(actionSubPackageName);
			File dir = ResourceUtil.getResourceAsFile(actionClasspath);
			Iterator<File> actionIter = FileUtils.iterateFiles(dir, EXTENSIONS, true);
			while(actionIter.hasNext()) {
				String relativePath = StringUtils.substring(actionIter.next().getPath(), dir.getPath().length() + 1).replace(File.separatorChar, '/');
				String path = ControllerUtil.fromClassNameToPath(StringUtil.trimSuffix(relativePath, actionSuffix + ".class"));
				controllers.add(path);
			}
		}

		return controllers;
	}

}
