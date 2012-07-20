package net.unit8.sastruts.routing.segment;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Segment;

public class DividerSegment extends StaticSegment {
	public DividerSegment(String value, Options options) {
		super(value, options);
	}
	
	public boolean isOptionalityImplied() {
		return true;
	}
}
