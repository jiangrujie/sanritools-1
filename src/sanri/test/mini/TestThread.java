package sanri.test.mini;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TestThread {
	public static void main(String[] args) {
		Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
		Iterator<Entry<Thread, StackTraceElement[]>> iterator = allStackTraces.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Thread, StackTraceElement[]> next = iterator.next();
			Thread key = next.getKey();
			System.out.println(key.getName());
		}
	}
}
