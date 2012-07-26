package net.unit8.sastruts.testapp.action;

import org.seasar.struts.annotation.Execute;

public class UserAction {
	@Execute(validator=false)
	public String list() {
		return "list.jsp";
	}
}
