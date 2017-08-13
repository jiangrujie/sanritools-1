package com.sanri.app.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.data.Stat;

import sanri.utils.PathUtil;

import com.sanri.app.BaseServlet;
import com.sanri.app.zookeeper.ZooConfig;
import com.sanri.app.zookeeper.ZooInspectorManager;
import com.sanri.app.zookeeper.ZooInspectorManagerImpl;
import com.sanri.app.zookeeper.ZooNodeACL;
import com.sanri.frame.RequestMapping;

/**
 * 
 * 创建时间:2017-7-22下午8:59:08<br/>
 * 创建者:sanri<br/>
 * 功能: zookeeper 数据处理<br/>
 */
@RequestMapping("/zoo")
public class ZooServlet extends BaseServlet{
	
	//连接名 == > 管理类
	public final static Map<String,ZooInspectorManager> ZOO_MAP = new HashMap<String, ZooInspectorManager>();
	//连接名 ==> 连接配置
	private final static Map<String,ZooConfig> CONNECTIONS = new HashMap<String, ZooConfig>();
	
	static{
		try {
			String configPath = PathUtil.pkgPath("com.sanri.config");
			PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(configPath+"/zookeeperdefault.properties");
			String connName = "default";
			String hosts = propertiesConfiguration.getString("hosts");
			String encryptionManager = propertiesConfiguration.getString("encryptionManager");
			String timeout = propertiesConfiguration.getString("timeout");
			String authScheme = propertiesConfiguration.getString("authScheme");
			String authData = propertiesConfiguration.getString("authData");
			
			ZooConfig zooConfig = new ZooConfig();
			zooConfig.setHost(hosts.split(":")[0]);
			zooConfig.setPort(Integer.parseInt(hosts.split(":")[1]));
			zooConfig.setEncryptionManager(encryptionManager);
			zooConfig.setTimeout(Integer.parseInt(timeout));
			zooConfig.setAuthData(authData);
			zooConfig.setAuthScheme(authScheme);
			zooConfig.setName(connName);
			
			CONNECTIONS.put(connName, zooConfig);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * 功能:获取所有的连接信息<br/>
	 * 创建时间:2017-7-23上午10:05:00<br/>
	 * 作者：sanri<br/>
	 * @return<br/>
	 */
	public Set<String> connections(){
		return CONNECTIONS.keySet();
	}
	
	/**
	 * 
	 * 功能:创建连接<br/>
	 * 创建时间:2017-7-23上午10:36:43<br/>
	 * 作者：sanri<br/><br/>
	 */
	public void createConn(ZooConfig config){
		String connName = config.getName();
		if(StringUtils.isBlank(connName)){
			throw new IllegalArgumentException("创建连接失败,需提供连接名称");
		}
		if(CONNECTIONS.containsKey(connName)){
			throw new IllegalArgumentException("连接名称已经存在:"+connName);
		}
		CONNECTIONS.put(connName, config);
	}
	
	/**
	 * 
	 * 功能:获取连接配置信息<br/>
	 * 创建时间:2017-7-23上午10:05:50<br/>
	 * 作者：sanri<br/>
	 * @param connName
	 * @return<br/>
	 */
	public ZooConfig connInfo(String connName){
		return CONNECTIONS.get(connName);
	}
	
	/**
	 * 
	 * 功能:列出子节点<br/>
	 * 创建时间:2017-7-23上午10:09:17<br/>
	 * 作者：sanri<br/>
	 * @return<br/>
	 */
	public List<String> childNodes(String connName,String nodePath) throws IllegalArgumentException{
		ZooInspectorManager zooInspectorManager = findZooInspectorManager(connName);
		if(zooInspectorManager != null){
			return zooInspectorManager.getChildren(nodePath);
		}
		return null;
	}
	
	/**
	 * 
	 * 功能:获取节点数据<br/>
	 * 创建时间:2017-7-23上午10:40:00<br/>
	 * 作者：sanri<br/>
	 * @param connName
	 * @param nodePath<br/>
	 */
	public String nodeData(String connName,String nodePath){
		ZooInspectorManager zooInspectorManager = findZooInspectorManager(connName);
		if(zooInspectorManager != null){
			return zooInspectorManager.getData(nodePath);
		}
		return null;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-24下午4:49:14<br/>
	 * 功能:获取节点元数据 <br/>
	 * @param connName
	 * @param nodePath
	 * @return
	 */
	public Stat nodeMeta(String connName,String nodePath){
		ZooInspectorManager findZooInspectorManager = findZooInspectorManager(connName);
		Stat nodeState = findZooInspectorManager.nodeState(nodePath);
		return nodeState;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-24下午6:34:07<br/>
	 * 功能:获取节点的 ACL 权限列表 <br/>
	 * @param connName
	 * @param nodePath
	 * @return
	 */
	public List<ZooNodeACL> nodeACLs(String connName,String nodePath){
		ZooInspectorManager findZooInspectorManager = findZooInspectorManager(connName);
		List<ZooNodeACL> nodeACLs = findZooInspectorManager.nodeACLs(nodePath);
		return nodeACLs;
	}
	
	/**
	 * 
	 * 功能:查找当前连接的管理实例<br/>
	 * 创建时间:2017-7-23上午10:12:35<br/>
	 * 作者：sanri<br/>
	 * @param connName
	 * @return<br/>
	 */
	private ZooInspectorManager findZooInspectorManager(String connName) throws IllegalArgumentException {
		ZooInspectorManager zooInspectorManager = ZOO_MAP.get(connName);
		if(zooInspectorManager == null){
			zooInspectorManager = new ZooInspectorManagerImpl();
			ZOO_MAP.put(connName, zooInspectorManager);
			Properties paramProperties = new Properties();
			ZooConfig zooConfig = CONNECTIONS.get(connName);
			if(zooConfig == null){
				throw new IllegalArgumentException("不存在的连接:"+connName);
			}
			paramProperties.setProperty("hosts", zooConfig.getHost()+":"+zooConfig.getPort());
			paramProperties.setProperty("timeout", String.valueOf(zooConfig.getTimeout()));
			paramProperties.setProperty("encryptionManager", zooConfig.getEncryptionManager());
			paramProperties.setProperty("authScheme", zooConfig.getAuthScheme());
			paramProperties.setProperty("authData", zooConfig.getAuthData());
			boolean connect = zooInspectorManager.connect(paramProperties );
			if(!connect){
				throw new IllegalArgumentException("连接失败:在 "+paramProperties.getProperty("hosts"));
			}
		}
		return zooInspectorManager;
	}
	
}
