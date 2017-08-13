package sanri.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.sanri.app.wscall.WsdlPort.SOAPType;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-26下午3:06:31<br/>
 * 功能:http 请求工具类 
 * 依赖 httpclient4.3.1,httpcore4.3.1
 * <br/>
 */
public class HttpUtil {
	private static Log logger = LogFactory.getLog(HttpUtil.class);
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-26下午3:53:48<br/>
	 * 功能:提交  xml 数据，返回 xml 数据 <br/>
	 * @param url 请求地址
	 * @param xml 数据
	 * @return
	 */
	private static ContentType XML_UTF8 = ContentType.create("text/xml", "utf-8");
	private static ContentType SOAP12_UTF8 = ContentType.create("application/xml+soap","utf-8");
	private HttpUtil(){}
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-26下午3:36:40<br/>
	 * 功能:兼容以前的功能，传入路径和参数，返回字符串的返回结果 <br/>
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException 
	 */
	public static String get(String url,Map<String,String> params) throws IOException,IllegalArgumentException{
		HttpClient httpClient = HttpClients.createDefault();
		List<NameValuePair> nameValuePairs = transferParam(params);
		HttpGet getMethod = null;
		if(!Validate.isEmpty(nameValuePairs)){
			HttpEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs,Consts.UTF_8);
			String keyValueParams = EntityUtils.toString(urlEncodedFormEntity ,Consts.UTF_8);
			getMethod = new HttpGet(url+"?"+keyValueParams);
		}else{
			getMethod = new HttpGet(url);
		}
		getMethod.addHeader("User-Agent", "sanri-user-agent");
		HttpResponse response  = null;
		try {
			response = httpClient.execute(getMethod);
			HttpEntity msgEntity = response.getEntity();
			String message = EntityUtils.toString(msgEntity);
			return message;
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			HttpClientUtils.closeQuietly(response);
			HttpClientUtils.closeQuietly(httpClient);
		}
		return "";
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-26下午3:36:40<br/>
	 * 功能:兼容以前的功能，传入路径和参数，返回字符串的返回结果 <br/>
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException 
	 */
	public static String post(String url,Map<String,String> params) throws IOException,IllegalArgumentException{
		HttpClient httpClient = HttpClients.createDefault();
		List<NameValuePair> nameValuePairs = transferParam(params);
		HttpPost postMethod = new HttpPost(url);
		if(!Validate.isEmpty(nameValuePairs)){
			HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,Consts.UTF_8);
			postMethod.setEntity(entity );
		}
		postMethod.addHeader("User-Agent", "sanri-user-agent");
		HttpResponse response  = null;
		try {
			response = httpClient.execute(postMethod);
			HttpEntity msgEntity = response.getEntity();
			String message = EntityUtils.toString(msgEntity,Consts.UTF_8);
			return message;
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			HttpClientUtils.closeQuietly(response);
			HttpClientUtils.closeQuietly(httpClient);
		}
		return "";
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-26下午5:59:57<br/>
	 * 功能:向路径提交 xml 信息 <br/>
	 * @param url
	 * @param xml
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static String postXml(String url,String xml) throws IOException,IllegalArgumentException{
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost postMethod = new HttpPost(url);
		postMethod.addHeader("Content-Type", "text/xml; charset=utf-8");
		postMethod.addHeader("User-Agent", "sanri-user-agent");
		HttpEntity xmlEntity = new StringEntity(xml,XML_UTF8);
		postMethod.setEntity(xmlEntity);
		HttpResponse response = null;
		long startTime = System.currentTimeMillis();
		try {
			logger.info("调用 "+url+" 参数为:\n"+xml);
			response = httpClient.execute(postMethod);
			logger.info("请求:"+url+" 所花时间 :"+(System.currentTimeMillis() - startTime));
			HttpEntity entity = response.getEntity();
			StatusLine statusLine = response.getStatusLine();
			long contentLength = entity.getContentLength();
			if(statusLine.getStatusCode() != 200){
				throw new RuntimeException("调用 "+url+" 返回错误码:"+statusLine.getStatusCode());
			}
			if(contentLength == 0){
				logger.warn("调用 "+url+" 返回数据长度为 0 ");
			}
			String xmlMsg = EntityUtils.toString(entity,Consts.UTF_8);
			return xmlMsg;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("客户端协议错误 ，检查 url 配置 url: "+url);
		} finally{
			HttpClientUtils.closeQuietly(response);
			HttpClientUtils.closeQuietly(httpClient);
		}
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-26下午4:37:25<br/>
	 * 功能:发送 soap 消息  <br/>
	 * @param url 请求路径 
	 * @param rawString xml 字符串
	 * @param soapType soap 协议类型
	 * @param soapAction soapAction
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static String postSoap(String url,String rawString,SOAPType soapType,String soapAction)throws IOException,IllegalArgumentException{
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost postMethod = new HttpPost(url);
		postMethod.addHeader("Content-Type", "text/xml; charset=utf-8");
		postMethod.addHeader("User-Agent", "sanri-user-agent");
		postMethod.addHeader("SOAPAction", soapAction);
		//获取请求内容 
		HttpEntity xmlEntity = null;
		if(soapType == SOAPType.SOAP11){
			xmlEntity = new StringEntity(rawString,XML_UTF8);
		}else if(soapType == SOAPType.SOAP12){
			xmlEntity = new StringEntity(rawString,SOAP12_UTF8);
		}else{
			throw new IllegalArgumentException("不支持的　soap 类型 "+soapType);
		}
		postMethod.setEntity(xmlEntity);
		HttpResponse response = null;
		long startTime = System.currentTimeMillis();
		try {
			response = httpClient.execute(postMethod);
			logger.info("请求:"+url+" 所花时间 :"+(System.currentTimeMillis() - startTime));
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity,Consts.UTF_8);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("客户端协议错误 ，检查 url 配置 url: "+url);
		} finally{
			HttpClientUtils.closeQuietly(response);
			HttpClientUtils.closeQuietly(httpClient);
		}
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-26下午3:47:02<br/>
	 * 功能:将 map 型 的参数转换为NameValuePair 类型  <br/>
	 * @param params
	 * @return
	 */
	public static List<NameValuePair> transferParam(Map<String, String> params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if(params != null && !params.isEmpty()){
			Iterator<Entry<String, String>> paramIterator = params.entrySet().iterator();
			while(paramIterator.hasNext()){
				Entry<String, String> param = paramIterator.next();
				nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}
		}
		return nameValuePairs;
	}
}