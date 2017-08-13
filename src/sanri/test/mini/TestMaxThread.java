package sanri.test.mini;

import java.util.concurrent.CountDownLatch;

public class TestMaxThread {
	public static void main(String[] args) {
		TestMaxThread testMaxThread = new TestMaxThread();
		testMaxThread.test();
	}

	public void test() {
		int count = 0;
		while (true) {
			new WorkThread().start();
			System.out.println(++count);
		}
	}

	class WorkThread extends Thread {
		CountDownLatch cdl = new CountDownLatch(1);

		public WorkThread() {
			this.setDaemon(true);
		}

		@Override
		public void run() {
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
