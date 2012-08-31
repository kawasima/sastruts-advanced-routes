package net.unit8.sastruts.routing;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.framework.util.FileInputStreamUtil;
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
	private String namespace = null;
	private String pathScope = null;
	private String moduleScope = null;
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
			SAXParser parser = SAXParserFactoryUtil.newSAXParser();
			SAXParserUtil.parse(parser, new InputSource(in), this);
		} finally {
			IOUtils.closeQuietly(in);
		}
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
			namespace = attributes.getValue("name");
			if (StringUtil.isEmpty(namespace)) {
				throw new SAXParseException("Can't find namespace's name.", locator);
			}
		} else if (qName.equalsIgnoreCase("scope")) {
			pathScope = attributes.getValue("name");
			moduleScope = attributes.getValue("module");
			if (StringUtil.isEmpty(pathScope) && StringUtil.isEmpty(moduleScope)) {
				throw new SAXParseException("Scope must have any attributes, name or module.", locator);
			}
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
		} else if (qName.equalsIgnoreCase("namespace")) {
			namespace = null;
		} else if (qName.equalsIgnoreCase("scope")) {
			pathScope = moduleScope = null;
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
			} else if (StringUtils.equals(optionName, "to")) {
				String[] tokens = StringUtils.split(attributes.getValue(i), "#", 2);
				if (tokens.length == 1) {
					options.$("action", tokens[0]);
				} else {
					options.$("controller", tokens[0]).$("action", tokens[1]);
				}
			} else if (StringUtils.equals(optionName, "via")){
				String[] methods = StringUtils.split(attributes.getValue(i), ",");
				for (String method : methods) {
					if (Routes.HTTP_METHODS.contains(method.toUpperCase())) {
						Options conditions = (Options)options.get("conditions");
						if (conditions == null) {
							conditions = new Options();
							options.put("conditions", conditions);
						}
						conditions.$("method", method.toUpperCase());
					}
				}
			} else {
				options.put(optionName, attributes.getValue(i));
			}
		}
		if (controller != null) {
			options.put("controller", controller);
		}
		if (namespace != null) {
			options.put("namespace", namespace);
			options.put("pathPrefix", namespace);
		}
		if (pathScope != null) {
			options.put("pathPrefix", pathScope);
		}
		if (moduleScope != null) {
			options.put("namespace", moduleScope);
		}
		return options;
	}
}
