package sanri.test.mini;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

//public private protected 默认
public class B {
	public static void main(String[] args) {
//		System.out.println("1,2".indexOf(1));
//		List<String> ab = new ArrayList<String>();
//		ab.add(String.valueOf(1));
//		ab.add(String.valueOf(2));
//		
//		boolean contains = ab.contains(String.valueOf(1));
//		System.out.println(contains);
		
	}
	
	@Test
	public void testTime(){
		String pattern = "yyyy-MM-dd HH:mm:ss";
		Date dateCurrent = new Date();
		Date whenSleepStart = DateUtils.setHours(dateCurrent, 5);
		Date whenSleepEnd = DateUtils.setHours(dateCurrent, 9);
		
		System.out.println(DateFormatUtils.format(dateCurrent, pattern));
		System.out.println(DateFormatUtils.format(whenSleepStart, pattern));
		System.out.println(DateFormatUtils.format(whenSleepEnd, pattern));
	}
}
