package com.sanri.app.zookeeper;

import java.util.List;

/**
 * 
 * 创建时间:2017-7-22下午8:44:42<br/>
 * 创建者:sanri<br/>
 * 功能:zookeeper 节点树管理<br/>
 */
public interface ZooInspectorNodeTreeManager {
	/**
	 * 
	 * 功能:创建节点<br/>
	 * 创建时间:2017-7-22下午8:44:54<br/>
	 * 作者：sanri<br/>
	 * @param parent
	 * @param nodeName
	 * @return<br/>
	 */
	boolean createNode(String parent, String nodeName);
	
	/**
	 * 
	 * 功能:删除节点<br/>
	 * 创建时间:2017-7-22下午8:46:03<br/>
	 * 作者：sanri<br/>
	 * @param nodePath
	 * @return<br/>
	 */
	boolean deleteNode(String nodePath);

	/**
	 * 
	 * 功能:是否有子节点<br/>
	 * 创建时间:2017-7-22下午8:46:10<br/>
	 * 作者：sanri<br/>
	 * @param nodePath
	 * @return<br/>
	 */
	boolean hasChildren(String nodePath);

	/**
	 * 
	 * 功能:获取节点在父节点中的位置<br/>
	 * 创建时间:2017-7-22下午8:46:18<br/>
	 * 作者：sanri<br/>
	 * @param nodePath
	 * @return<br/>
	 */
	int getNodeIndex(String nodePath);

	/**
	 * 
	 * 功能:获取子节点数量<br/>
	 * 创建时间:2017-7-22下午8:48:54<br/>
	 * 作者：sanri<br/>
	 * @param nodePath
	 * @return<br/>
	 */
	int getNumChildren(String nodePath);

	/**
	 * 
	 * 功能:获取第 childIndex 个子节点<br/>
	 * 创建时间:2017-7-22下午8:49:06<br/>
	 * 作者：sanri<br/>
	 * @param nodePath
	 * @param childIndex
	 * @return<br/>
	 */
	String getNodeChild(String nodePath, int childIndex);
	
	/**
	 * 
	 * 功能:是否允许有子节点<br/>
	 * 创建时间:2017-7-22下午8:49:25<br/>
	 * 作者：sanri<br/>
	 * @param nodePath
	 * @return<br/>
	 */
	boolean isAllowsChildren(String nodePath);

	/**
	 * 
	 * 功能:获取第一级子节点<br/>
	 * 创建时间:2017-7-22下午8:49:38<br/>
	 * 作者：sanri<br/>
	 * @param nodePath
	 * @return<br/>
	 */
	List<String> getChildren(String nodePath);
}