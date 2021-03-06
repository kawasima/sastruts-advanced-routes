package net.unit8.sastruts.routing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;

import net.unit8.sastruts.ARStringUtil;

import org.seasar.framework.util.FileInputStreamUtil;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.SAXParserFactoryUtil;
import org.seasar.framework.util.SAXParserUtil;
import org.seasar.framework.util.StringUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class RouteLoader extends DefaultHandler {
	private String controller = null;
	private Stack<String> pathScope = new Stack<String>();
	private Stack<String> moduleScope = new Stack<String>();
	private Locator locator;
	private RouteBuilder builder;
	private Options options;
	private String path;
	private List<Route> routes;

	public RouteLoader(RouteBuilder builder) {
		this.routes = new ArrayList<Route>();
		this.builder = builder;
	}

	public List<Route> load(File config) {
		FileInputStream in = FileInputStreamUtil.create(config);
		try {
			return load(in);
		} finally {
			InputStreamUtil.close(in);
		}
	}

	public List<Route> load(InputStream stream) {
		SAXParser parser = SAXParserFactoryUtil.newSAXParser();
		SAXParserUtil.parse(parser, new InputSource(stream), this);
		return routes;
	}

	@Override
	public void setDocumentLocator(final Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("match") || Routes.HTTP_METHODS.contains(qName.toUpperCase())) {
			path = attributes.getValue("path");
			if (StringUtil.isEmpty(path)) {
				throw new SAXParseException("Can't find path in match.", locator);
			}
			options = processAttributes(attributes);
			if (!qName.equalsIgnoreCase("match")) {
				Options conditions = (Options)options.get("conditions");
				if (conditions == null) {
					conditions = new Options();
					options.put("conditions", conditions);
				}
				conditions.$("method", qName.toUpperCase());
			}
		} else if (qName.equalsIgnoreCase("controller")) {
			controller = attributes.getValue("name");
			if (StringUtil.isEmpty(controller)) {
				throw new SAXParseException("Can't find controller name.", locator);
			}
		} else if (qName.equalsIgnoreCase("root")) {
			Options options = processAttributes(attributes);
			routes.add(builder.build("/", options));
		} else if (qName.equalsIgnoreCase("namespace")) {
			String name = attributes.getValue("name");
			if (StringUtil.isEmpty(name)) {
				throw new SAXParseException("Can't find namespace's name.", locator);
			}
			pathScope.push(name);
			moduleScope.push(name);
		} else if (qName.equalsIgnoreCase("scope")) {
			String name = ARStringUtil.defaultIfEmpty(attributes.getValue("name"), "");
			String module = ARStringUtil.defaultIfEmpty(attributes.getValue("module"), "");
			if (StringUtil.isEmpty(name) && StringUtil.isEmpty(module)) {
				throw new SAXParseException("Scope must have any attributes, name or module.", locator);
			}
			pathScope.push(name);
			moduleScope.push(module);
		} else if (qName.equalsIgnoreCase("requirements")) {
			options.$("requirements", new Options());
		} else if (qName.equalsIgnoreCase("requirement")) {
			Options requirements = (Options)options.get("requirements");
			if (requirements == null) {
				throw new SAXParseException("Requirement must be in the requirements.", locator);
			}
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			requirements.$(name, Pattern.compile(value));
		} else if (qName.equalsIgnoreCase("defaults")) {
			options.$("defaults", new Options());
		} else if (qName.equalsIgnoreCase("default")) {
			Options defaults = (Options)options.get("defaults");
			if (defaults == null) {
				throw new SAXParseException("Default must be in the defaults.", locator);
			}
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			defaults.$(name, value);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("match")  || Routes.HTTP_METHODS.contains(qName.toUpperCase())) {
			routes.add(builder.build(path, options));
			options = null;
		} else if (qName.equalsIgnoreCase("controller")) {
			controller = null;
		} else if (qName.equalsIgnoreCase("namespace") || qName.equalsIgnoreCase("scope")) {
			pathScope.pop();
			moduleScope.pop();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

	private Options processAttributes(Attributes attributes) {
		int attrLen = attributes.getLength();
		Options options = new Options();
		for (int i=0; i < attrLen; i++) {
			String optionName = attributes.getQName(i);
			if (StringUtil.equals(optionName, "path")) {
				continue;
			} else if (StringUtil.equals(optionName, "to")) {
				String[] tokens = ARStringUtil.split(attributes.getValue(i), "#", 2);
				if (tokens.length == 1) {
					options.$("action", tokens[0]);
				} else {
					options.$("controller", tokens[0]).$("action", tokens[1]);
				}
			} else if (StringUtil.equals(optionName, "via")){
				String[] methods = StringUtil.split(attributes.getValue(i), ",");
				for (String method : methods) {
					if (Routes.HTTP_METHODS.contains(method.trim().toUpperCase())) {
						Options conditions = (Options)options.get("conditions");
						if (conditions == null) {
							conditions = new Options();
							options.put("conditions", conditions);
						}

						List<Object> methodList;
						if (!conditions.containsKey("method")) {
							methodList = new ArrayList<Object>();
							conditions.$("method", methodList);
						} else {
							methodList = conditions.getList("method");
						}
						methodList.add(method.trim().toUpperCase());
					}
				}
			} else {
				options.put(optionName, attributes.getValue(i));
			}
		}
		if (controller != null) {
			options.put("controller", controller);
		}
		if (!pathScope.empty()) {
			String pathPrefix = ARStringUtil.strip(ARStringUtil.join(pathScope, '/').replaceAll("/+", "/"), "/");
			if (StringUtil.isNotEmpty(pathPrefix) && !StringUtil.equals(pathPrefix, "/"))
				options.put("pathPrefix", pathPrefix);
		}
		if (!moduleScope.empty()) {
			String namespace = ARStringUtil.strip(ARStringUtil.join(moduleScope, '/').replaceAll("/+", "/"), "/");
			if (StringUtil.isNotEmpty(namespace) && !StringUtil.equals(namespace, "/"))
				options.put("namespace", namespace);
		}
		return options;
	}
}
