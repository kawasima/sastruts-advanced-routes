package net.unit8.sastruts.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.annotation.RegisterNamingConvention;

@RunWith(Seasar2.class)
@RegisterNamingConvention(false)
public class RouteSetTest {
	@Test
	public void recognize() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/post/", new Options().$("controller", "post"));
		routeSet.addRoute("/post/:id",
				new Options().$("controller", "post").$("action", "show"));
		Route route = routeSet.addRoute("/post/:id/comments",
				new Options().$("controller", "post").$("action", "comments"));
		System.out.println(route);
		Options params = routeSet.recognizePath("/post/1/comments");
		assertThat(params.getString("controller"), is("post"));
		System.out.println(params);
	}

	@Test
	public void recognizeControllerWithSubPackage() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/post/", new Options().$("controller", "user.Post"));
		routeSet.addRoute("/post/:id",
				new Options().$("controller", "user.Post").$("action", "show"));
		Route route = routeSet.addRoute(
				"/post/:id/comments",
				new Options().$("controller", "user.Post").$("action",
						"comments"));
		System.out.println(route);
		Options params = route.recognize("/post/1/comments");
		assertThat(params.getString("controller"), is("user.Post"));
		System.out.println(params);
	}

	@Test
	public void recognizeIndex() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/post/", new Options().$("controller", "user.Post"));
		routeSet.addRoute("/post/:id",
				new Options().$("controller", "user.Post").$("action", "show"));
		routeSet.addRoute(
				"/post/:id/comments",
				new Options().$("controller", "user.Post").$("action",
						"comments"));
		routeSet.addRoute("/", new Options().$("controller", "Index"));
		Options params = routeSet.recognizePath("/");
		assertThat(params.getString("controller"), is("Index"));
		assertThat(params.getString("action"), is("index"));
	}

	@Test
	public void recognizeController() {
		RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/:controller/:action", new Options());
		Options params = routeSet.recognizePath("/user/list");
		assertThat(params.getString("controller"), is("User"));
		assertThat(params.getString("action"), is("list"));

		params = routeSet.recognizePath("/admin/proof");
		assertThat(params.getString("controller"), is("admin.Proof"));
		assertThat(params.getString("action"), is("index"));
	}
	@Test
	public void recognizeMultiThread() {
		final RouteSet routeSet = new RouteSet();
		routeSet.addRoute("/foo/:id", new Options().$("controller", "foo"));
		routeSet.addRoute("/bar/:id", new Options().$("controller", "bar"));
		//
		int testCount = 1000;
		ExecutorService exec = Executors.newCachedThreadPool();
		List<RoutingTask> tasks = new ArrayList<RoutingTask>(testCount);
		for(int i=0; i < testCount; i++) {
			tasks.add(new RoutingTask(i, i%2 == 0 ? "foo" : "bar", routeSet));
		}
		try {
			List<Future<RoutingTaskResult>> result = exec.invokeAll(tasks);
			for(Future<RoutingTaskResult> actual : result) {
				assertTrue(actual.isDone());
				RoutingTaskResult taskResult = actual.get();
				assertTrue(taskResult.message, taskResult.success);
			}
		} catch (Exception e) {
			System.out.println(e);
			fail();
		} finally {
			exec.shutdown();
		}
	}

	private static final class RoutingTaskResult {
		public boolean success;
		public String message = "";
	}
	private static final class RoutingTask implements Callable<RoutingTaskResult> {
		
		private final String id;
		private final String controller;
		private final String path;
		private final RouteSet routeset;
		
		public RoutingTask(int id, String controller, RouteSet routeset) {
			this.id = Integer.toString(id);
			this.controller = controller;
			this.routeset = routeset;
			this.path = String.format("/%s/%s", this.controller, this.id);
		}

		public RoutingTaskResult call() throws Exception {
			// TODO 自動生成されたメソッド・スタブ
			RoutingTaskResult result = new RoutingTaskResult();
			final Options options = routeset.recognizePath(path);
			result.success = id.equals(options.getString("id")) && controller.equals(options.getString("controller"));
			if (!result.success) {
				result.message = String.format("Failed. id:[%s], controller:[%s], path:[%s], recognizePath:[%s]", id, controller, path, options);
			}
			return result;
		}
	}
}
