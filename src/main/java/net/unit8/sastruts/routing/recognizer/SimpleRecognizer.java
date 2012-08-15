package net.unit8.sastruts.routing.recognizer;

import java.util.List;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Recognizer;
import net.unit8.sastruts.routing.Route;
import net.unit8.sastruts.routing.segment.RoutingException;

public class SimpleRecognizer extends Recognizer {
	private List<Route> routes;

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	public Options recognize(String path) {
		for (Route route : routes) {
			Options result = route.recognize(path);
			if (result != null) return result;
		}
		throw new RoutingException("No route matches " + path);
	}

	@Override
	public boolean isOptimized() {
		return true;
	}

	@Override
	public void optimize() {
		// nop
	}
}
