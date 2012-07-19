package net.unit8.sastruts.routing;

public class RouteOptions {
	private String pathPrefix;

	public String getPathPrefix() {
		return (pathPrefix == null) ? "" : pathPrefix;
	}

	public void except(String...keys) {
		if (keys != null) {
			for (String key : keys) {

			}
		}
	}

	public boolean hasNamespace() {
		return false;
	}

	public String getNamespace() {
		return null;
	}

	public String getController() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public void setController(String string) {
	}
}
