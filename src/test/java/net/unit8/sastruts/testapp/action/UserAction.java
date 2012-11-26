package net.unit8.sastruts.testapp.action;

import org.seasar.struts.annotation.Execute;

public class UserAction {
	@Execute(validator = false)
	public String list() {
		return "list.jsp";
	}

	@Execute(validator = false)
	public String create() {
		return "create.jsp";
	}

	@Execute(validator = false)
	public String edit() {
		return "edit.jsp";
	}

	@Execute(validator = false, redirect = true)
	public String delete() {
		return "list";
	}
}
