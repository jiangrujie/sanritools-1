package com.sanri.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sanri.utils.PathUtil;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-22下午2:42:25<br/>
 * 功能:通用 servlet 实现一些通用功能 <br/>
 */
public class BaseServlet {
	protected Log logger = LogFactory.getLog(getClass());
	protected static File dataTempPath = null;
	static{
		dataTempPath = new File(PathUtil.webAppsPath()+"/temp");
		if(!dataTempPath.exists()){
			dataTempPath.mkdir();
		}
	}
	//初始创建 10 个线程的线程池,所有线程往线程池中提交
	protected static ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	public final static String datetimePattern  = "yyyy-MM-dd HH:mm:ss";
	public final static String default_path = "/";
	public final static int default_age = 365 * 24 * 3600;
	public final static Charset charset = Charset.forName("utf-8");

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午2:19:45<br/>
	 * 功能:向客户端添加缓存数据 <br/>
	 * @param key 键
	 * @param value 值
	 * @param response response 对象
	 * @param age 有效期以秒为单位
	 */
	protected void addCookie(HttpServletResponse response,String key, String value, int age,String path) {
		Cookie cookie = new Cookie(key,new String(Base64.encodeBase64(value.getBytes(charset))));
		cookie.setMaxAge(age);
		cookie.setPath(path);
		response.addCookie(cookie);
	}
	protected void addCookie(HttpServletResponse response,String key, String value,String path) {
		addCookie(response,key, value,default_age,path);
	}
	protected void addCookie(HttpServletResponse response,String key, String value) {
		addCookie(response,key, value,default_age,default_path);
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午2:25:34<br/>
	 * 功能:删除客户端缓存<br/>
	 * @param key 缓存键
	 * @param response
	 * 思路:建立同名 cookie 来覆盖删除,直接写在 / 目录,才能保证删除
	 */
	protected void deleteCookie(HttpServletResponse response,String key) {
		Cookie cookie = new Cookie(key, "");
		cookie.setMaxAge(0);
		cookie.setPath(default_path);
		response.addCookie(cookie);
	}
	
	/**
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午2:23:00<br/>
	 * 功能:查找客户端缓存数据 <br/>
	 * @param key 缓存键
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected Cookie findCookie(HttpServletRequest request,String key) throws UnsupportedEncodingException {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(key)) {
					return cookie;
				}
			}
		}
		return null;
	}
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午2:30:53<br/>
	 * 功能:获取到 cookie 中存储的值解码 base64 <br/>
	 * @param cookie
	 * @return
	 */
	protected String cookieValue(Cookie cookie){
		return new String(Base64.decodeBase64(cookie.getValue().getBytes()),charset);
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午2:43:43<br/>
	 * 功能:获取客户端请求 ip <br/>
	 * @param request
	 * @return
	 */
    protected String remortIPInfo(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    /**
     * 
     * 作者:sanri <br/>
     * 时间:2017-7-22下午2:53:20<br/>
     * 功能:获取客户端信息,有可能客户端不是用浏览器访问而返回空  <br/>
     * 可以获取客户端操作系统,浏览器类型,浏览器版本等信息
     * @param request
     * @return 
     */
    protected UserAgent remoteAgentInfo(HttpServletRequest request){
    	String userAgentString = request.getHeader("User-Agent");
		if(StringUtils.isBlank(userAgentString)){
			return null;
		}
		userAgentString = userAgentString.toLowerCase();
		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
		return userAgent;
    }

	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午2:33:12<br/>
	 * 功能: 通用下载<br/>
	 * @param downloadName 下载到客户端文件名称
	 * @param inputStream 文件输入流
	 */
	protected void download(HttpServletRequest request,HttpServletResponse response,String downloadName,InputStream inputStream){
		OutputStream os  = null;
		try {
			os = response.getOutputStream();  
			response.reset();  
			
			String agent = request.getHeader("USER-AGENT");    
			if (null != agent){    
			    if (-1 != agent.indexOf("Firefox")) {//Firefox    
			    	downloadName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(downloadName.getBytes("UTF-8"))))+ "?=";    
			    }else if (-1 != agent.indexOf("Chrome")) {//Chrome    
			    	downloadName = new String(downloadName.getBytes(), "ISO8859-1");    
			    } else {//IE7+    
			    	downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");    
			    	downloadName = StringUtils.replace(downloadName, "+", "%20");//替换空格    
			    } 
			}
			response.setHeader("Content-Disposition", "attachment; filename="+ downloadName);
			IOUtils.copy(inputStream, os);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(os);
		}
	}
}
