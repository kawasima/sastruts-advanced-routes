package net.unit8.sastruts.routing;

import java.util.HashMap;
import java.util.Map;

import org.seasar.framework.util.IntegerConversionUtil;
import org.seasar.framework.util.StringUtil;

public class Options extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;

	public Options(Map<String, Object> options) {
		super(options);
	}

	public Options() {
	}

	public Options $(String key, Object value) {
		this.put(key, value);
		return this;
	}
	public String getString(String key) {
		Object value = this.get(key);
		return value == null ? "" : value.toString();
	}
	public Options except(String...keys) {
		Options copy = new Options(this);
		for (String key : keys) {
			copy.remove(key);
		}
		return copy;
	}
	public boolean getBoolean(String key) {
		Object value = this.get(key);
		if (value != null && (
				(value instanceof Boolean && (Boolean)value) ||
				(value instanceof Number && IntegerConversionUtil.toPrimitiveInt(value) == 1) ||
				StringUtil.equals(value.toString(), "true")
				)) {
			return true;
		}
		return false;
	}
	public Options takeoutOptions(String key) {
		Object obj = this.remove(key);
		if (obj instanceof Options) {
			return (Options)obj;
		} else {
			return new Options();
		}
	}
}
