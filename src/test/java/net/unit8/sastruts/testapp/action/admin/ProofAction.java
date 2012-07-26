package net.unit8.sastruts.testapp.action.admin;

import org.seasar.struts.annotation.Execute;

public class ProofAction {
	@Execute(validator=false)
	public String index() {
		return "index.jsp";
	}
}
