package net.unit8.sastruts.testapp.action.admin.nested;

import org.seasar.struts.annotation.Execute;

public class FooAction {
	@Execute(validator = false)
	public String index() {
		return "index.jsp";
	}
}
