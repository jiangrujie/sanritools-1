package sanri.test.mini;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

public class TTT {
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -8);
		System.out.println(DateFormatUtils.format(cal, "yyyy-MM-dd"));
	}
}
