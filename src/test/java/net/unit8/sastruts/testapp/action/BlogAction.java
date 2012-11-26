package net.unit8.sastruts.testapp.action;

import org.seasar.struts.annotation.Execute;

public class BlogAction {
	@Execute(validator = true)
	public String comments() {
		return "comments.jsp";
	}

}
