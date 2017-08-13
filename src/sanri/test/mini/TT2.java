package sanri.test.mini;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

public class TT2 {
	public static void main(String[] args) {
		Date beginDate = new Date();
		 Date addDays = DateUtils.addDays(beginDate, 4);
		 System.out.println(DateFormatUtils.format(addDays, "yyyy-MM-dd"));
	}
}
