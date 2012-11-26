package net.unit8.sastruts.routing.recognizer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OptimizedRecognizerTest {

	@Test
	public void test() {
		OptimizedRecognizer recognizer = new OptimizedRecognizer();
		String[] segments = recognizer
				.toPlainSegments("/images/(:width)x(:height)");
		assertThat(segments.length, is(4));
		assertThat(segments[1], is(":width"));
	}

}
