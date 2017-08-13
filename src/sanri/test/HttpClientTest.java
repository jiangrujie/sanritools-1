package sanri.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import sanri.utils.HttpUtil;
import sanri.utils.PathUtil;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-26上午10:06:15<br/>
 * 功能:httpclient 测试，依赖包 
 * httpclient4.3.1
 * httpcore 4.3.1
 *  <br/>
 */
public class HttpClientTest {
	
	@Test
	public void testGet(){
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
//			String urlStr = "http://192.168.0.95:8081/newsanritools/sqlclient/connections";
			String urlStr = "http://192.168.0.95:8081/newsanritools/sqlclient/schemas";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("connName", "default"));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params , Consts.UTF_8);
			String keyValueParams = EntityUtils.toString(urlEncodedFormEntity);
			HttpGet getMethod = new HttpGet(urlStr+"?"+keyValueParams);
			CloseableHttpResponse httpResponse = httpClient.execute(getMethod);
			try {
				StatusLine statusLine = httpResponse.getStatusLine();
				System.out.println("响应状态码:"+statusLine.getStatusCode());
				System.out.println("响应消息:"+statusLine.getReasonPhrase());
				if(statusLine.getStatusCode() == 200){
					HttpEntity msgEntity = httpResponse.getEntity();
					Header contentType = msgEntity.getContentType();
					System.out.println("返回消息类型:"+contentType);
					System.out.println("结果:"+EntityUtils.toString(msgEntity, Consts.UTF_8));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				httpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-27下午5:07:17<br/>
	 * 功能:新保确认 <br/>
	 * @throws IOException
	 */
	@Test
	public void testXml() throws IOException{
		String pkgPath = PathUtil.pkgPath("sanri.test");
		File file = new File(pkgPath+"/0402.xml");
		String xml = FileUtils.readFileToString(file ,"utf-8");
		System.out.println(xml);
		String postXml = HttpUtil.postXml("http://agenttest.sinosafe.com.cn/websales", xml);
		System.out.println(postXml);
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-27下午5:24:13<br/>
	 * 功能:根据保单号查询保单 <br/>
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	@Test
	public void testQuery() throws IllegalArgumentException, IOException{
		String pkgPath = PathUtil.pkgPath("sanri.test");
		File file = new File(pkgPath+"/0402query.xml");
		String xml = FileUtils.readFileToString(file ,"utf-8");
		System.out.println(xml);
		String postXml = HttpUtil.postXml("http://agenttest.sinosafe.com.cn/websales", xml);
		System.out.println(postXml);
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-27下午5:25:31<br/>
	 * 功能:退保确认 <br/>
	 * @throws IOException
	 */
	@Test
	public void testCancel() throws IOException{
		String pkgPath = PathUtil.pkgPath("sanri.test");
		File file = new File(pkgPath+"/0402cancel.xml");
		String xml = FileUtils.readFileToString(file ,"utf-8");
		System.out.println(xml);
		String postXml = HttpUtil.postXml("http://agenttest.sinosafe.com.cn/websales", xml);
		System.out.println(postXml);
	}
	
	@Test
	public void testPost(){
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
//			String urlStr = "http://192.168.0.95:8081/newsanritools/sqlclient/connections";
			String urlStr = "http://192.168.0.95:8081/newsanritools/sqlclient/schemas";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("connName", "default"));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params , Consts.UTF_8);
			HttpPost postMethod = new HttpPost(urlStr);
			postMethod.setEntity(urlEncodedFormEntity);			//post 方法可以设置消息载体，get 不行，只能拼拉到 url 后面
			CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
			try {
				StatusLine statusLine = httpResponse.getStatusLine();
				System.out.println("响应状态码:"+statusLine.getStatusCode());
				System.out.println("响应消息:"+statusLine.getReasonPhrase());
				if(statusLine.getStatusCode() == 200){
					HttpEntity msgEntity = httpResponse.getEntity();
					Header contentType = msgEntity.getContentType();
					System.out.println("返回消息类型:"+contentType);
					System.out.println("结果:"+EntityUtils.toString(msgEntity, Consts.UTF_8));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				httpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testCookieStore(){
		String loginUrl = "http://127.0.0.1:8080/hhx-gps-web/user/login";
		CookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore ).build();
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userName", "admin"));
			params.add(new BasicNameValuePair("passWord", "admin"));
			params.add(new BasicNameValuePair("checkCode", "111"));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params , Consts.UTF_8);
			HttpPost postMethod = new HttpPost(loginUrl);
			postMethod.setEntity(urlEncodedFormEntity);			
			CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
			httpResponse.close();
			List<Cookie> cookies = cookieStore.getCookies();
			System.out.println(cookies.size());
			System.out.println(cookies);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testLogin(){
		String loginUrl = "http://127.0.0.1:8080/hhx-gps-web/user/login";
		String dataUrl = "http://127.0.0.1:8080/hhx-gps-web/user/findAllUserByConditions";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userName", "admin"));
			params.add(new BasicNameValuePair("passWord", "admin"));
			params.add(new BasicNameValuePair("checkCode", "111"));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params , Consts.UTF_8);
			HttpPost postMethod = new HttpPost(loginUrl);
			postMethod.setEntity(urlEncodedFormEntity);			
			CloseableHttpResponse httpResponse = httpClient.execute(postMethod);
			try {
				StatusLine statusLine = httpResponse.getStatusLine();
				if(statusLine.getStatusCode() == 200){
					//默认是登录成功的，不再解析返回信息
					//httpclient4.x 默认是记住了登录会话的,只要 httpclient 不关的话，不需要自己 再带 cookie 设置 
					HttpGet getMethod = new HttpGet(dataUrl);
					CloseableHttpResponse getHttpResponse = httpClient.execute(getMethod);
					HttpEntity entity = getHttpResponse.getEntity();
					System.out.println(EntityUtils.toString(entity));
					getHttpResponse.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				httpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
