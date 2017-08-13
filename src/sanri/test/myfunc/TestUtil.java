package sanri.test.myfunc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import sanri.utils.HttpUtil;
import sanri.utils.ZipUtil;

public class TestUtil {

	@Test
	public void testHttpUtil(){
		Map<String,String> params = new HashMap<String, String>();
		params.put("key", "9e1a5b02ea5c452baae71259cd8823a3");
		params.put("userid", "1");
		params.put("info", "你好");
		String post = "";
		try {
			post = HttpUtil.get("http://www.tuling123.com/openapi/api", params);
			System.out.println(post);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGet(){
		Map<String, String> params = new HashMap<String, String>();
		try {
			String string = HttpUtil.get("http://192.168.0.95:8081/newsanritools/sqlclient/connections", params );
			System.out.println(string);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testZipUtil(){
		File input = new File("E:\\project\\xxx\\pretools\\sanritools\\WebRoot");
		ZipUtil.zip(input, new File("d:/test/webroot.zip"));
		//测试空目录
//		File input = new File("D:\\test\\a");
//		ZipUtil.zip(input, new File("d:/test/a.zip"));
		//测试单文件
//		File input = new File("d:/test/webroot.zip");
//		ZipUtil.zip(input, new File("d:/test/web.zip"));
	}
	
	@Test
	public void testUnzip(){
		File zipFile = new File("d:/test/webroot.zip");
		ZipUtil.unzip(zipFile, "d:/test/webroot");
	}
}
