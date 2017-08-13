package com.sanri.frame;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;

import com.sanri.app.servlet.PostmanServlet;
import com.sanri.app.servlet.ZooServlet;
import com.sanri.app.zookeeper.ZooInspectorManager;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-26下午8:56:47<br/>
 * 功能:上下文监听器,当窗口销毁时,销毁创建的全局变量 <br/>
 * 当调试时点击 eclipse 结束 tomcat 并不是正常结束 ,不会调用 contextDestroyed ; 但关闭进程会关闭进程的所有资源,所以也会释放
 */
public class ContextLoaderListener implements ServletContextListener{

	private Log logger = LogFactory.getLog(ContextLoaderListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		//目前只要销毁 PostmanServlet 中的所有 httpclient,以后需要使用注释来批量释放 
		Iterator<Entry<String, HttpClient>> httpClientIterator = PostmanServlet.clientMap.entrySet().iterator();
		while(httpClientIterator.hasNext()){
			Entry<String, HttpClient> httpClientEntry = httpClientIterator.next();
			String key = httpClientEntry.getKey();
			logger.info("释放 http 会话:"+key);
			HttpClient httpClient = httpClientEntry.getValue();
			HttpClientUtils.closeQuietly(httpClient);
		}
		//释放  zookeeper 连接
		Iterator<Entry<String, ZooInspectorManager>> zooIterator = ZooServlet.ZOO_MAP.entrySet().iterator();
		while(zooIterator.hasNext()){
			Entry<String, ZooInspectorManager> zooEntry = zooIterator.next();
			String key = zooEntry.getKey();
			logger.info("释放 zookeeper 连接:"+key);
			ZooInspectorManager zooInspectorManager = zooEntry.getValue();
			zooInspectorManager.disconnect();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
	}

}
