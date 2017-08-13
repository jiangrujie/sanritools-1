package sanri.test.mini;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

public class MM {
//	public MM() throws RuntimeException{
//		throw new RuntimeException("运行时异常");
//	}
//	
//	public static void main(String [] args) {
//		MM mm  = null;
//		try {
//			mm = new MM();
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//			System.out.println(mm);
//		}
//	}
//	public static void main(String[] args) {
//		String contentFormat = "实例:%s,[1:gps可以解析,2:gprs可以解析,4:gps 轨迹回放数据可以获取],解析能力为(%s)";
//		System.out.println(String.format(contentFormat, "liliang",7));
//		System.out.println(contentFormat);
//		double abc = 12.324234325;
//		String format = String.format("%.2f",0d);
//		System.out.println(format);
		
//		System.out.println(test2());
//	}
	
	@Test
	public void testSort(){
		MailSendQueue mailSendQueue = new MailSendQueue();
		mailSendQueue.setPriority(1);
		MailSendQueue mailSendQueue2 = new MailSendQueue();
		mailSendQueue2.setPriority(5);
		
		List<MailSendQueue> mailSendQueues = new ArrayList<MailSendQueue>();
		mailSendQueues.add(mailSendQueue2);
		mailSendQueues.add(mailSendQueue);
		
		
		System.out.println(mailSendQueues);
		Collections.sort(mailSendQueues);
		System.out.println(mailSendQueues);
		
	}
	
	public static String test2(){
		try {
			if(DateFormatUtils.format(-1, "xxx") != null){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ok";
	}
	
	@Test
	public void test4(){
		String test3 = test3();
		System.out.println(test3);
	}
	
	public String test3(){
		String noHandledGroupIds = "1,3";
		String noHandledDate = "2017-07-03";
		String noHandledActiveDate = "2017-07-03";
		
		if(DateUtil.compareDate(noHandledDate,DateUtil.formatDateYmd(DateUtil.parseDate("2017-03-11 00:00:00")))
				||DateUtil.compareDate(noHandledActiveDate, DateUtil.formatDateYmd(DateUtil.parseDate(null)))){
			System.out.println("1");
		}
		return "1233";
	}
	
}
