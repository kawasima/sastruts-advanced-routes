package net.unit8.sastruts.routing;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class OptionsTest {
	@Test
	public void getListByEmpty() {
		Options options = new Options();
		List<Object> list = options.getList("hoge");
		assertThat(list.isEmpty(), is(true));
	}

	@Test
	public void getListByArray() {
		Options options = new Options();
		options.$("hoge", new String[] { "foo", "bar" });
		List<Object> list = options.getList("hoge");
		assertThat(list.size(), equalTo(2));
		assertThat((String) list.get(0), equalTo("foo"));
		assertThat((String) list.get(1), equalTo("bar"));
	}

	@Test
	public void getListByString() {
		Options options = new Options();
		options.$("hoge", "moga");
		List<Object> list = options.getList("hoge");
		assertThat(list.size(), equalTo(1));
		assertThat((String) list.get(0), equalTo("moga"));
	}

	@Test
	public void getListByList() {
		Options options = new Options();
		options.$("hoge", Arrays.asList("foo", "bar"));
		List<Object> list = options.getList("hoge");
		assertThat(list.size(), equalTo(2));
		assertThat((String) list.get(0), equalTo("foo"));
		assertThat((String) list.get(1), equalTo("bar"));
	}

	@Test
	public void getListByCollection() {
		Options options = new Options();
		Set<String> value = new LinkedHashSet<String>();
		value.add("foo");
		value.add("bar");
		options.$("hoge", value);
		List<Object> list = options.getList("hoge");
		assertThat(list.size(), equalTo(2));
		assertThat((String) list.get(0), equalTo("foo"));
		assertThat((String) list.get(1), equalTo("bar"));
	}
}
