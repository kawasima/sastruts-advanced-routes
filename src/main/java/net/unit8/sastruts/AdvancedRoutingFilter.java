package net.unit8.sastruts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.unit8.sastruts.routing.Options;
import net.unit8.sastruts.routing.Routes;
import net.unit8.sastruts.routing.segment.RoutingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.exception.NoSuchMethodRuntimeException;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.LongConversionUtil;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;
import org.seasar.struts.config.S2ExecuteConfig;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.S2ExecuteConfigUtil;
import org.seasar.struts.util.URLEncoderUtil;

/**
 * Routing filter like Ruby on Rails.
 *
 * @author kawasima
 *
 */
public class AdvancedRoutingFilter implements Filter {
	private static final Logger logger = Logger.getLogger(AdvancedRoutingFilter.class);
	private static volatile boolean loading = false;
	/**
	 * 最後にroutes設定を読み込んだ時刻が入ります。
	 */
	private static long lastLoaded = -1;

	/**
	 * JSPのダイレクトアクセスを許すかどうかです。
	 */
	protected boolean jspDirectAccess = false;

	/**
	 * ルート定義ファイルのパスです。
	 */
	protected File routes;

	/**
	 * ルート定義ファイルの更新チェックをする時間を決めます。
	 *
	 */
	protected Long checkInterval;

	/**
	 * If contextSensitive is true, recognize the path after context path and generate the path with context path.
	 */
	protected boolean contextSensitive = false;

	/**
	 * Header name stands for request-uri.
	 * This parameter is used with a reverse proxy.
	 */
	protected String requestUriHeader;

	/**
	 * Whether the routing exception fall through.
	 */
	protected boolean fallThrough = false;

	public void init(FilterConfig config) throws ServletException {
		String access = config.getInitParameter("jspDirectAccess");
		if (StringUtil.isNotBlank(access)) {
			jspDirectAccess = Boolean.valueOf(access);
		}

		String routesPath = config.getInitParameter("routes");
		if (StringUtil.isNotEmpty(routesPath)) {
			String realRoutesPath = config.getServletContext().getRealPath(routesPath);
			if (realRoutesPath != null) {
				routes = new File(realRoutesPath);
			}
			InputStream routesStream = config.getServletContext().getResourceAsStream(routesPath);
			try {
				Routes.load(routesStream);
			} finally {
				IOUtils.closeQuietly(routesStream);
			}
			lastLoaded = System.currentTimeMillis();
		}

		String interval = config.getInitParameter("checkInterval");
		if (StringUtil.isNotEmpty(interval)) {
			checkInterval = LongConversionUtil.toLong(interval);
		}
		if (checkInterval == null || checkInterval < 0) {
			checkInterval = -1L;
		}

		String contextSensitiveParam = config.getInitParameter("contextSensitive");
		if (StringUtil.isNotBlank(contextSensitiveParam)) {
			contextSensitive = Boolean.valueOf(contextSensitiveParam);
		}
		if (contextSensitive) {
			try {
				Method getServletContext = ReflectionUtil.getMethod(ServletContext.class, "getServletContext");
				UrlRewriter.contextPath = (String)MethodUtil
						.invoke(getServletContext, config.getServletContext(), null);
			} catch (NoSuchMethodRuntimeException e) {
				UrlRewriter.contextPath = config.getServletContext().getServletContextName();
			}
		}
		requestUriHeader = config.getInitParameter("requestUriHeader");

		String fallThroughParam = config.getInitParameter("fallThrough");
		if (StringUtil.isNotBlank(fallThroughParam)) {
			fallThrough = Boolean.valueOf(fallThroughParam);
		}

	}

	public void destroy() {
	}

	private void reloadRoutes() {
		if (loading || routesIsNotFile()) {
			return;
		}
		if (lastLoaded < 0 || checkInterval >= 0 && System.currentTimeMillis() > lastLoaded + checkInterval * 1000) {
			synchronized(this) {
				if (!loading)
					loading = true;
				else
					return;
			}
			if (loading) {
				try {
					logger.debug("check update for routes.");
					if (routes.lastModified() > lastLoaded) {
						long t1 = System.currentTimeMillis();
						Routes.load(routes);
						long t2 = System.currentTimeMillis();
						logger.debug(String.format("reload routes(%dms).", (t2 - t1)));
					}
					lastLoaded = System.currentTimeMillis();
				} finally {
					loading = false;
				}
			}
		}
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
		reloadRoutes();

		if (path.indexOf('.') < 0) {
			// If the request pass via reverse proxy, the original path must be gotten from HTTP header.
			if (!contextSensitive) {
				path = getOriginalPath(req);
			}
			try {
				Options options = Routes.recognizePath(path);
				String controller = options.getString("controller");
				String action = options.getString("action");
				Options params = options.except("controller", "action");

				String actionPath = ControllerUtil.fromClassNameToPath(controller);
				S2Container container = SingletonS2ContainerFactory.getContainer();
				if (container.hasComponentDef(actionPath.replace('/', '_').concat("Action"))) {
					S2ExecuteConfig executeConfig;
					if (StringUtil.equals(action, "index")) {
						executeConfig = S2ExecuteConfigUtil.findExecuteConfig("/" + actionPath, req);
						action = executeConfig.getMethod().getName();
					} else {
						executeConfig = S2ExecuteConfigUtil.findExecuteConfig("/" + actionPath, action);
					}
					if (executeConfig != null) {
						StringBuilder forwardPath = new StringBuilder(256);
						forwardPath.append("/").append(actionPath).append(".do?SAStruts.method=").append(URLEncoderUtil.encode(action));
						for(String key : params.keySet()) {
							forwardPath.append("&").append(URLEncoderUtil.encode(key))
								.append("=").append(URLEncoderUtil.encode(params.getString(key)));
						}
						logger.debug(String.format("recognize route %s as %s#%s.", path, actionPath, action));
						req.getRequestDispatcher(forwardPath.toString()).forward(req, res);
						return;
					}
				}
			} catch(RoutingException e) {
				if (!fallThrough)
					throw e;
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

	protected String getOriginalPath(HttpServletRequest req) {
		String path = StringUtil.isEmpty(requestUriHeader) ? req.getRequestURI() : req.getHeader(requestUriHeader);
		if (path == null)
			return "";

		int len = path.length();
		int i=0;
		for (; i < len; i++)
			if (path.charAt(i) == '?' || path.charAt(i) == ';')
				break;
		if (i != len)
			path = StringUtils.substring(path, 0, i);
		return path;
	}

	private boolean routesIsNotFile() {
		return routes == null;
	}
}
