package net.unit8.sastruts.routing;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;

import org.apache.commons.io.IOUtils;
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
	private Locator locator;
	private RouteSet routeSet;

	public RouteLoader(RouteSet routeSet) {
		this.routeSet = routeSet;
	}
	public synchronized void load(File config) {
		FileInputStream in = FileInputStreamUtil.create(config);
		try {
			SAXParser parser = SAXParserFactoryUtil.newSAXParser();
			SAXParserUtil.parse(parser, new InputSource(in), this);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("match")) {
			String path = attributes.getValue("path");
			if (StringUtil.isEmpty(path)) {
				throw new SAXParseException("Can't find path in match.", locator);
			}
			int attrLen = attributes.getLength();
			Options options = new Options();
			for (int i=0; i < attrLen; i++) {
				String optionName = attributes.getQName(i);
				if (StringUtil.equals(optionName, "path"))
					continue;
				options.put(optionName, attributes.getValue(i));
			}
			if (controller != null) {
				options.put("controller", controller);
			}
			routeSet.addRoute(path, options);
		} else if (qName.equalsIgnoreCase("controller")) {
			controller = attributes.getValue("name");
			if (StringUtil.isEmpty(controller)) {
				throw new SAXParseException("Can't find controller name.", locator);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("controller")) {
			controller = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}
}
