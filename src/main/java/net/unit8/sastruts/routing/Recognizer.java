package net.unit8.sastruts.routing;

import java.util.List;

public abstract class Recognizer {
	public abstract void setRoutes(List<Route> routes);
	public abstract Options recognize(String path);
}
