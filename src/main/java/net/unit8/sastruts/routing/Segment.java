package net.unit8.sastruts.routing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.framework.util.URLUtil;

public abstract class Segment {
	public static final String RESERVED_PCHAR = ":@&=+$,;";

	private String value;
	private boolean isOptional;

	public Segment() {
		isOptional = false;
	}

	public int numberOfCaptures() {
		return Pattern.compile(regexpChunk()).matcher("").groupCount();
	}

	public String getExtractionCode() {
		return null;
	}

	public String continueStringStructure(LinkedList<Segment> priorSegments) {
		if (priorSegments.isEmpty()) {
			return interpolationStatement(priorSegments);
		} else {
			LinkedList<Segment> newPriors = priorSegments;
			return priorSegments.getLast().stringStructure(newPriors);
		}
	}
	public String interpolationChunk() {
		return URLUtil.encode(value, "UTF-8" /* TODO enable to set charset */);
	}

	public String interpolationStatement(LinkedList<Segment> priorSegments) {
		StringBuilder chunks = new StringBuilder(128);
		for(Segment seg : priorSegments) {
			chunks.append(seg.interpolationChunk());
		}
		chunks.append(interpolationChunk());
		return "\"" + chunks.toString() + "\"" + allOptionalsAvailableCondition(priorSegments);
	}

	public String stringStructure(LinkedList<Segment> priorSegments) {
		return isOptional ? continueStringStructure(priorSegments) : interpolationStatement(priorSegments);
	}

	public String allOptionalsAvailableCondition(LinkedList<Segment> priorSegments) {
		return null;
	}

	public void matchExtraction(Options params, Matcher match, int nextCapture) {
	}

	public boolean hasKey() {
		return false;
	}

	public String getKey() {
		return null;
	}

	public abstract String regexpChunk();
	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean optional) {
		this.isOptional = optional;
	}

	public void setRegexp(Pattern regexp) {}
	public void setDefault(Object def) {}

	public String buildPattern(String pattern) {
		return null;
	}

	public String getRegexp() {
		return null;
	}
}
