package net.unit8.sastruts.routing;

import java.util.HashMap;

import org.seasar.framework.util.IntegerConversionUtil;
import org.seasar.framework.util.NumberConversionUtil;
import org.seasar.framework.util.StringUtil;

public class Options extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;

	public Options(Options options) {
		super(options);
	}
	public String getString(String key) {
		Object value = this.get(key);
		return value == null ? null : value.toString();
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
}
