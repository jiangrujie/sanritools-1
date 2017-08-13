//package sanri.test.myfunc;
//
//import java.io.IOException;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.sanri.app.osclient.LinuxClient;
//
//import expect4j.Expect4j;
//
///**
// * 
// * 创建时间:2017-7-2下午2:58:18<br/>
// * 创建者:sanri<br/>
// * 功能:linux 客户端测试<br/>
// */
//public class TestLinuxClient {
//	
//	static String ip,user,password;
//	 static int port;
//	
//	@BeforeClass
//	public static void init(){
//		ip = "192.168.124.132";
//		user= "root";
//		password="h196944";
//		port = 22;
//	}
//	
//	@Test
//	public void testSendCmd(){
//		LinuxClient linuxClient = new LinuxClient(ip, port, user, password);
//		try {
//			Expect4j shell = linuxClient.getShell("default");
//			String sendCommand = linuxClient.sendCommand(shell, "w\r");
//			System.out.println(sendCommand);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//}
