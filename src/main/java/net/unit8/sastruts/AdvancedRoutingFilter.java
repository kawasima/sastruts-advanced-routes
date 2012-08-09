package net.unit8.sastruts;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Routes;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.URLEncoderUtil;

public class AdvancedRoutingFilter implements Filter {

	/**
	 * JSPのダイレクトアクセスを許すかどうかです。
	 */
	protected boolean jspDirectAccess = false;

	/**
	 * ルート定義ファイルのパスです。
	 */
	protected String routes;

	public void init(FilterConfig config) throws ServletException {
		String access = config.getInitParameter("jspDirectAccess");
		if (!StringUtil.isBlank(access)) {
			jspDirectAccess = Boolean.valueOf(access);
		}

		String routes = config.getInitParameter("routes");
		if (StringUtil.isNotEmpty(routes)) {
			String path = config.getServletContext().getRealPath(routes);
			Routes.load(new File(path));
		}
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String contextPath = req.getContextPath();
		if (contextPath.equals("/")) {
			contextPath = "";
		}
		String path = RequestUtil.getPath(req);
		if (!processDirectAccess(request, response, chain, path)) {
			return;
		}
		if (path.indexOf('.') < 0) {
			Options options = Routes.recognizePath(path);
			String controller = options.getString("controller");
			String action = options.getString("action");
			String actionPath = ControllerUtil.fromClassNameToPath(controller);
			S2Container container = SingletonS2ContainerFactory.getContainer();
			if (container.hasComponentDef(actionPath.replace('/', '_').concat("Action"))) {
				S2ExecuteConfig executeConfig = S2ExecuteConfigUtil.findExecuteConfig("/" + actionPath, action);
				if (executeConfig != null) {
					String forwardPath = "/" + actionPath + ".do?SAStruts.method=" + URLEncoderUtil.encode(action);
					req.getRequestDispatcher(forwardPath).forward(req, res);
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * ダイレクトアクセスを処理します。
	 *
	 * @param request
	 *            リクエスト
	 * @param response
	 *            レスポンス
	 * @param chain
	 *            フィルタチェイン
	 * @param path
	 *            パス
	 * @return JSPのダイレクトアクセスのチェックがNGの場合は、 falseを返します。
	 * @throws IOException
	 *             IO例外が発生した場合。
	 */
	protected boolean processDirectAccess(ServletRequest request,
			ServletResponse response, FilterChain chain, String path)
			throws IOException {
		if (!jspDirectAccess
				&& ((HttpServletRequest) request).getMethod().equalsIgnoreCase(
						"get") && path.endsWith(".jsp")) {
			String message = "Direct access for JSP is not permitted.";
			if (path.endsWith("index.jsp")) {
				message += " Remove \"index.jsp\" from welcome-file-list of (default) \"web.xml\".";
			}
			((HttpServletResponse) response).sendError(
					HttpServletResponse.SC_BAD_REQUEST, message);
			return false;
		}
		return true;
	}

	/**
	 * Strutsのサーブレットにフォワードします。
	 *
	 * @param request
	 *            リクエスト
	 * @param response
	 *            レスポンス
	 * @param actionPath
	 *            アクションパス
	 * @param paramPath
	 *            パラメータのパス
	 * @param executeConfig
	 *            実行設定
	 * @throws IOException
	 *             IO例外が発生した場合
	 * @throws ServletException
	 *             サーブレット例外が発生した場合
	 */
	protected void forward(HttpServletRequest request,
			HttpServletResponse response, String actionPath, String paramPath,
			S2ExecuteConfig executeConfig) throws IOException, ServletException {
		String forwardPath = actionPath + ".do";
		if (executeConfig != null) {
			forwardPath = forwardPath + executeConfig.getQueryString(paramPath);
		}
		request.getRequestDispatcher(forwardPath).forward(request, response);
	}

}
