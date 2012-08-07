package core.test;

import sun.misc.*;

public class SignalHandlerTest {
	public static void main(String... args) throws Exception {
		SignalHandler handler = new SignalHandler() {
			public void handle(Signal signal) {
				System.out.println(signal.getName());
				System.exit(-1);
			}
		};
		// Signal.handle(new Signal("KILL"), handler);//�൱��kill -9
		Signal.handle(new Signal("TERM"), handler);// �൱��kill -15
		Signal.handle(new Signal("INT"), handler);// �൱��Ctrl+C
		for (;;) {
			Thread.sleep(1000);
		}
	}
}
