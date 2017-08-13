package com.sanri.app.zookeeper;

import java.util.List;
import java.util.Map;

import org.apache.zookeeper.data.Stat;

/**
 * 
 * 创建时间:2017-7-22下午8:40:56<br/>
 * 创建者:sanri<br/>
 * 功能:zookeeper 节点管理<br/>
 */
public interface ZooNodeManager {
	/**
	 * 
	 * 功能:设置数据<br/>
	 * 创建时间:2017-7-22下午8:41:21<br/>
	 * 作者：sanri<br/>
	 * @param nodePath 节点
	 * @param data 数据
	 * @return<br/>
	 */
	boolean setData(String nodePath, String data);

	/**
	 * 功能:获取节点数据<br/>
	 * 创建时间:2017-7-22下午8:42:02<br/>
	 * 作者：sanri<br/>
	 * @param nodePath 节点
	 * @return<br/>
	 */
	String getData(String nodePath);

	/**
	 * 
	 * 功能:获取节点元数据<br/>
	 * 创建时间:2017-7-22下午8:42:18<br/>
	 * 作者：sanri<br/>
	 * @param nodePath 节点
	 * @return<br/>
	 */
	Map<String, String> getNodeMeta(String nodePath);
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-24下午6:21:53<br/>
	 * 功能:获取节点 acl 权限列表 <br/>
	 * @param nodePath
	 * @return
	 */
	List<ZooNodeACL> nodeACLs(String nodePath);
	
	Stat nodeState(String nodePath) ;
	
	/**
	 * 
	 * 功能:获取节点 acl 权限<br/>
	 * 创建时间:2017-7-22下午8:42:27<br/>
	 * 作者：sanri<br/>
	 * @param nodePath
	 * @return<br/>
	 */
	List<Map<String, String>> getACLs(String nodePath);
}
