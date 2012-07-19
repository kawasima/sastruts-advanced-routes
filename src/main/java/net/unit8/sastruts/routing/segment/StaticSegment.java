package net.unit8.sastruts.routing.segment;

import net.unit8.sastruts.routing.Segment;

public class StaticSegment extends Segment {
	private String value;
	private boolean raw;

	public StaticSegment(String value) {
		super();
		this.value = value;
	}

	public StaticSegment(String value, boolean optional) {
		super();
		this.value = value;
	}

	@Override
	public String interpolationChunk() {
		return value;
	}

	public String regexpChunk() {
		return value;
	}

	public int getNumberOfCaptures() {
		return 0;
	}

	@Override
	public String toString() {
		return value;
	}
}
