package com.sanri.app.zookeeper;

import java.util.Map;
import java.util.Properties;

public interface ZooInspectorManager extends ZooInspectorNodeTreeManager,ZooNodeManager {
	/**
	 * 
	 * 功能:连接 zk <br/>
	 * 创建时间:2017-7-22下午8:36:02<br/>
	 * 作者：sanri<br/>
	 * @param paramProperties
	 * @return<br/>
	 */
	boolean connect(Properties paramProperties);
	
	/**
	 * 
	 * 功能:断开连接<br/>
	 * 创建时间:2017-7-22下午8:36:55<br/>
	 * 作者：sanri<br/>
	 * @return<br/>
	 */
	boolean disconnect();
	
	/**
	 * 
	 * 功能:获取连接元数据<br/>
	 * 创建时间:2017-7-22下午8:38:42<br/>
	 * 作者：sanri<br/>
	 * @return<br/>
	 */
	Map<String, String> getSessionMeta();


}