package com.sanri.app.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import com.sanri.app.zookeeper.encryption.BasicDataEncryptionManager;
import com.sanri.app.zookeeper.encryption.DataEncryptionManager;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-22下午5:46:18<br/>
 * 功能: zookeeper 管理实例<br/>
 */
public class ZooInspectorManagerImpl implements ZooInspectorManager {
	private DataEncryptionManager encryptionManager;
	private String hostPort;
	private int sessionTimeout;
	private ZooKeeper zooKeeper;
	protected boolean connected = true;
	
	private Log logger = LogFactory.getLog(getClass());

	public ZooInspectorManagerImpl(){}

	@Override
	public boolean connect(Properties connectionProps) {
		try {
			if (this.zooKeeper == null) {
				String hostPort = connectionProps.getProperty("hosts");
				String sessionTimeout = connectionProps.getProperty("timeout");
				String encryptionManager = connectionProps.getProperty("encryptionManager");
				String authScheme = connectionProps.getProperty("authScheme");
				String authData = connectionProps.getProperty("authData");

				if ((hostPort == null) || (sessionTimeout == null)) {
					throw new IllegalArgumentException("需要主机和超时配置:hosts:"+hostPort+",timeout:"+sessionTimeout);
				}
				if (encryptionManager == null) {
					this.encryptionManager = new BasicDataEncryptionManager();
				} else {
					Class<?> clazz = Class.forName(encryptionManager);

					if (Arrays.asList(clazz.getInterfaces()).contains(DataEncryptionManager.class))
						this.encryptionManager = ((DataEncryptionManager) Class.forName(encryptionManager).newInstance());
					else {
						throw new IllegalArgumentException("encryptionManager 必须实现  DataEncryptionManager 接口");
					}
				}
				this.hostPort = hostPort;
				this.sessionTimeout = Integer.valueOf(sessionTimeout).intValue();
				this.zooKeeper = new ZooKeeperRetry(hostPort, Integer.valueOf(sessionTimeout).intValue(), new Watcher() {
					public void process(WatchedEvent event) {
						if (event.getState() == Watcher.Event.KeeperState.Expired)
							ZooInspectorManagerImpl.this.connected = false;
					}
				});
				if ((authData != null) && (authData.length() > 0)) {
					this.zooKeeper.addAuthInfo(authScheme, authData.getBytes());
				}
				((ZooKeeperRetry) this.zooKeeper).setRetryLimit(10);
				this.connected = ((ZooKeeperRetry) this.zooKeeper).testConnection();
			}
		} catch (Exception e) {
			this.connected = false;
			e.printStackTrace();
		}
		if (!this.connected) {
			disconnect();
		}
		return this.connected;
	}

	@Override
	public boolean disconnect() {
		try {
			if (this.zooKeeper != null) {
				this.zooKeeper.close();
				this.zooKeeper = null;
				this.connected = false;
				return true;
			}
		} catch (Exception e) {
			logger.error("Error occurred while disconnecting from ZooKeeper server", e);
		}
		return false;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:获取第一级子节点列表  <br/>
	 * @throws IOException
	 */
	@Override
	public List<String> getChildren(String nodePath) {
		if (this.connected) {
			try {
				return this.zooKeeper.getChildren(nodePath, false);
			} catch (Exception e) {
				logger.error(new StringBuilder().append("Error occurred retrieving children of node: ").append(nodePath).toString(), e);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:获取节点数据  <br/>
	 * @throws IOException
	 */
	@Override
	public String getData(String nodePath) {
		if (this.connected) {
			try {
				if (nodePath.length() == 0) {
					nodePath = "/";
				}
				Stat nodeStat = this.zooKeeper.exists(nodePath, false);
				if (nodeStat != null)
					return this.encryptionManager.decryptData(this.zooKeeper.getData(nodePath, false, nodeStat));
			} catch (Exception e) {
				logger.error(new StringBuilder().append("获取节点数据错误: ").append(nodePath).toString(), e);
			}
		}
		return null;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:获取子节点  <br/>
	 * @param childIndex 位置从  0 开始 
	 * @throws IOException
	 */
	@Override
	public String getNodeChild(String nodePath, int childIndex) {
		if (this.connected) {
			try {
				Stat nodeStat = this.zooKeeper.exists(nodePath, false);
				if (nodeStat != null) {
					List<String> list = this.zooKeeper.getChildren(nodePath, false);
					Collections.sort(list);
					return list.get(childIndex);
				}
			} catch (Exception e) {
				logger.error(new StringBuilder().append("获取子节点出错 ").append(childIndex).append(" 节点: ").append(nodePath).toString(),e);
			}
		}
		return null;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:获取子节点位置索引 <br/>
	 * @throws IOException
	 */
	@Override
	public int getNodeIndex(String nodePath) {
		if (this.connected) {
			int index = nodePath.lastIndexOf("/");
			if ((index == -1) || ((!nodePath.equals("/")) && (nodePath.charAt(nodePath.length() - 1) == '/'))) {
				throw new IllegalArgumentException(new StringBuilder().append("Invalid node path: ").append(nodePath).toString());
			}
			String parentPath = nodePath.substring(0, index);
			String child = nodePath.substring(index + 1);
			if ((parentPath != null) && (parentPath.length() > 0)) {
				List<String> children = getChildren(parentPath);
				if (children != null) {
					return children.indexOf(child);
				}
			}
		}
		return -1;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:获取 ACL 权限 ?? <br/>
	 * @throws IOException
	 */
	@Override
	public List<Map<String, String>> getACLs(String nodePath) {
		List<Map<String, String>> returnACLs = new ArrayList<Map<String, String>>();
		if (this.connected) {
			try {
				if (nodePath.length() == 0) {
					nodePath = "/";
				}
				Stat s = this.zooKeeper.exists(nodePath, false);
				if (s != null) {
					List<ACL> acls = this.zooKeeper.getACL(nodePath, s);
					for (ACL acl : acls) {
						Map<String,String> aclMap = new LinkedHashMap<String,String>();
						aclMap.put("scheme", acl.getId().getScheme());
						aclMap.put("id", acl.getId().getId());
						StringBuilder sb = new StringBuilder();
						int perms = acl.getPerms();
						boolean addedPerm = false;
						if ((perms & 0x1) == 1) {
							sb.append("Read");
							addedPerm = true;
						}
						if (addedPerm) {
							sb.append(", ");
						}
						if ((perms & 0x2) == 2) {
							sb.append("Write");
							addedPerm = true;
						}
						if (addedPerm) {
							sb.append(", ");
						}
						if ((perms & 0x4) == 4) {
							sb.append("Create");
							addedPerm = true;
						}
						if (addedPerm) {
							sb.append(", ");
						}
						if ((perms & 0x8) == 8) {
							sb.append("Delete");
							addedPerm = true;
						}
						if (addedPerm) {
							sb.append(", ");
						}
						if ((perms & 0x10) == 16) {
							sb.append("Admin");
							addedPerm = true;
						}
						aclMap.put("permissions", sb.toString());
						returnACLs.add(aclMap);
					}
				}
			} catch (InterruptedException e) {
				logger.error(new StringBuilder().append("Error occurred retrieving ACLs of node: ").append(nodePath).toString(), e);
			} catch (KeeperException e) {
				logger.error(new StringBuilder().append("Error occurred retrieving ACLs of node: ").append(nodePath).toString(), e);
			}
		}
		return returnACLs;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:获取节点元数据 <br/>
	 * @throws IOException
	 */
	@Override
	public Map<String, String> getNodeMeta(String nodePath) {
		Map<String, String> nodeMeta = new LinkedHashMap<String, String>();
		if (this.connected) {
			try {
				if (nodePath.length() == 0) {
					nodePath = "/";
				}
				Stat nodeStat = this.zooKeeper.exists(nodePath, false);
				if (nodeStat != null) {
					nodeMeta.put("ACL Version", String.valueOf(nodeStat.getAversion()));
					nodeMeta.put("Creation Time", String.valueOf(nodeStat.getCtime()));
					nodeMeta.put("Children Version", String.valueOf(nodeStat.getCversion()));
					nodeMeta.put("Creation ID", String.valueOf(nodeStat.getCzxid()));
					nodeMeta.put("Data Length", String.valueOf(nodeStat.getDataLength()));
					nodeMeta.put("Ephemeral Owner", String.valueOf(nodeStat.getEphemeralOwner()));
					nodeMeta.put("Last Modified Time", String.valueOf(nodeStat.getMtime()));
					nodeMeta.put("Modified ID", String.valueOf(nodeStat.getMzxid()));
					nodeMeta.put("Number of Children", String.valueOf(nodeStat.getNumChildren()));
					nodeMeta.put("Node ID", String.valueOf(nodeStat.getPzxid()));
					nodeMeta.put("Data Version", String.valueOf(nodeStat.getVersion()));
				}
			} catch (Exception e) {
				logger.error(new StringBuilder().append("获取节点元数据出错 node: ").append(nodePath).toString(), e);
			}
		}
		return nodeMeta;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-24下午4:47:18<br/>
	 * 功能:获取节点元数据 <br/>
	 * @param nodePath
	 * @return
	 */
	@Override
	public Stat nodeState(String nodePath){
		try {
			Stat nodeStat = this.zooKeeper.exists(nodePath, false);
			return nodeStat;
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:获取节点数量  <br/>
	 * @throws IOException
	 */
	@Override
	public int getNumChildren(String nodePath) {
		if (this.connected) {
			try {
				Stat s = this.zooKeeper.exists(nodePath, false);
				if (s != null)
					return s.getNumChildren();
			} catch (Exception e) {
				logger.error(new StringBuilder().append("Error occurred getting the number of children of node: ").append(nodePath).toString(), e);
			}
		}
		return -1;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:判断是否有子节点<br/>
	 * @throws IOException
	 */
	@Override
	public boolean hasChildren(String nodePath) {
		return getNumChildren(nodePath) > 0;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:判断是否允许有子节点 取的是 stat 中的 ephemeralOwner 字段 是否 == 0 <br/>
	 * @throws IOException
	 */
	@Override
	public boolean isAllowsChildren(String nodePath) {
		if (this.connected) {
			try {
				Stat s = this.zooKeeper.exists(nodePath, false);
				if (s != null)
					return s.getEphemeralOwner() == 0L;
			} catch (Exception e) {
				logger.error(new StringBuilder().append("判断是否允许有子节点出错: ").append(nodePath).toString(), e);
			}
		}
		return false;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:session 元数据<br/>
	 * @throws IOException
	 */
	@Override
	public Map<String, String> getSessionMeta() {
		Map<String, String> sessionMeta = new LinkedHashMap<String, String>();
		try {
			if (this.zooKeeper != null) {
				sessionMeta.put("Session ID", String.valueOf(this.zooKeeper.getSessionId()));
				sessionMeta.put("Session State", String.valueOf(this.zooKeeper.getState().toString()));
				sessionMeta.put("hosts", this.hostPort);
				sessionMeta.put("timeout", String.valueOf(this.sessionTimeout));
			}
		} catch (Exception e) {
			logger.error("Error occurred retrieving session meta data.", e);
		}
		return sessionMeta;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:创建节点<br/>
	 * @throws IOException
	 */
	@Override
	public boolean createNode(String parent, String nodeName) {
		if (this.connected) {
			try {
				String[] nodeElements = nodeName.split("/");
				for (String nodeElement : nodeElements) {
					String node = new StringBuilder().append(parent).append("/").append(nodeElement).toString();
					Stat s = this.zooKeeper.exists(node, false);
					if (s == null) {
						this.zooKeeper.create(node, this.encryptionManager.encryptData(null), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
						parent = node;
					}
				}
				return true;
			} catch (Exception e) {
				logger.error(new StringBuilder().append("Error occurred creating node: ").append(parent).append("/").append(nodeName).toString(), e);
			}
		}
		return false;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:递归删除节点  <br/>
	 * @throws IOException
	 */
	@Override
	public boolean deleteNode(String nodePath) {
		if (this.connected) {
			try {
				Stat s = this.zooKeeper.exists(nodePath, false);
				if (s != null) {
					List<String> children = this.zooKeeper.getChildren(nodePath, false);
					for (String child : children) {
						String node = new StringBuilder().append(nodePath).append("/").append(child).toString();
						deleteNode(node);
					}
					this.zooKeeper.delete(nodePath, -1);
				}
				return true;
			} catch (Exception e) {
				logger.error(new StringBuilder().append("Error occurred deleting node: ").append(nodePath).toString(), e);
			}
		}
		return false;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午5:50:51<br/>
	 * 功能:设置数据  <br/>
	 * @throws IOException
	 */
	@Override
	public boolean setData(String nodePath, String data) {
		if (this.connected) {
			try {
				this.zooKeeper.setData(nodePath, this.encryptionManager.encryptData(data), -1);
				return true;
			} catch (Exception e) {
				logger.error(new StringBuilder().append("Error occurred setting data for node: ").append(nodePath).toString(), e);
			}
		}
		return false;
	}

	public class NodeWatcher implements Watcher {
		private final String nodePath;
		private final NodeListener nodeListener;
		private final ZooKeeper zookeeper;
		private boolean closed = false;

		public NodeWatcher(String nodePath, NodeListener nodeListener, ZooKeeper zookeeper) throws KeeperException, InterruptedException {
			this.nodePath = nodePath;
			this.nodeListener = nodeListener;
			this.zookeeper = zookeeper;
			Stat s = ZooInspectorManagerImpl.this.zooKeeper.exists(nodePath, this);
			if (s != null)
				zookeeper.getChildren(nodePath, this);
		}

		public void process(WatchedEvent event) {
			if (!this.closed) {
				try {
					if (event.getType() != Watcher.Event.EventType.NodeDeleted) {
						Stat s = ZooInspectorManagerImpl.this.zooKeeper.exists(this.nodePath, this);
						if (s != null)
							this.zookeeper.getChildren(this.nodePath, this);
					}
				} catch (Exception e) {
					logger.error("Error occured re-adding node watcherfor node " + this.nodePath, e);
				}
				this.nodeListener.processEvent(event.getPath(), event.getType().name(), null);
			}
		}

		public void stop() {
			this.closed = true;
		}
	}

	/**
	 * 对原有 acl 权限进行解析
	 */
	@Override
	public List<ZooNodeACL> nodeACLs(String nodePath) {
		List<ZooNodeACL> returnACLs = new ArrayList<ZooNodeACL>();
		if (this.connected) {
			try {
				if (nodePath.length() == 0) {
					nodePath = "/";
				}
				Stat stat = this.zooKeeper.exists(nodePath, false);
				if (stat != null) {
					List<ACL> acls = this.zooKeeper.getACL(nodePath, stat);
					for (ACL acl : acls) {
						ZooNodeACL zooNodeACL = new ZooNodeACL(acl.getId().getScheme(), acl.getId().getId(), acl.getPerms());
						returnACLs.add(zooNodeACL);
					}
				}
			} catch (InterruptedException e) {
				logger.error(new StringBuilder().append("Error occurred retrieving ACLs of node: ").append(nodePath).toString(), e);
			} catch (KeeperException e) {
				logger.error(new StringBuilder().append("Error occurred retrieving ACLs of node: ").append(nodePath).toString(), e);
			}
		}
		return returnACLs;
	}

}