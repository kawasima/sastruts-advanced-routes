package net.unit8.sastruts.routing.segment;

import net.unit8.sastruts.routing.Options;

public class DividerSegment extends StaticSegment {
	public DividerSegment(String value, Options options) {
		super(value, options.$("raw", true).$("optional", true));
	}

	public DividerSegment(String value) {
		this(value, new Options());
	}
	public DividerSegment() {
		this(null, new Options());
	}

	public boolean isOptionalityImplied() {
		return true;
	}
}
