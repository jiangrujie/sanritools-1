package com.sanri.app.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperRetry extends ZooKeeper {
	private boolean closed = false;
	private final Watcher watcher;
	private int limit = -1;
	private Log logger = LogFactory.getLog(getClass());

	public ZooKeeperRetry(String connectString, int sessionTimeout, Watcher watcher) throws IOException {
		super(connectString, sessionTimeout, watcher);
		this.watcher = watcher;
	}

	public ZooKeeperRetry(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd) throws IOException {
		super(connectString, sessionTimeout, watcher, sessionId, sessionPasswd);
		this.watcher = watcher;
	}

	public synchronized void close() throws InterruptedException {
		this.closed = true;
		super.close();
	}

	public String create(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws KeeperException, InterruptedException {
		int count = 0;
		do {
			try {
				return super.create(path, data, acl, createMode);
			} catch (KeeperException.ConnectionLossException e) {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (exists(path, false) != null) {
					return path;
				}
			} catch (KeeperException.NodeExistsException e) {
				return path;
			}
		} while ((!this.closed) && ((this.limit == -1) || (count++ < this.limit)));
		return null;
	}

	public void delete(String path, int version) throws InterruptedException, KeeperException {
		int count = 0;
		do {
			try {
				super.delete(path, version);
			} catch (KeeperException.ConnectionLossException e) {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (exists(path, false) == null) {
					return;
				}
			} catch (KeeperException.NoNodeException e) {
				break;
			}
		} while ((!this.closed) && ((this.limit == -1) || (count++ < this.limit)));
	}

	public Stat exists(String path, boolean watch) throws KeeperException, InterruptedException {
		int count = 0;
		try {
			return super.exists(path, watch ? this.watcher : null);
		} catch (KeeperException.ConnectionLossException e) {
			do {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (this.closed) {
					break;
				}
			} while ((this.limit == -1) || (count++ < this.limit));
		}
		return null;
	}

	public Stat exists(String path, Watcher watcher) throws KeeperException, InterruptedException {
		int count = 0;
		try {
			return super.exists(path, watcher);
		} catch (KeeperException.ConnectionLossException e) {
			do {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (this.closed) {
					break;
				}
			} while ((this.limit == -1) || (count++ < this.limit));
		}
		return null;
	}

	public List<ACL> getACL(String path, Stat stat) throws KeeperException, InterruptedException {
		int count = 0;
		try {
			return super.getACL(path, stat);
		} catch (KeeperException.ConnectionLossException e) {
			do {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (this.closed) {
					break;
				}
			} while ((this.limit == -1) || (count++ < this.limit));
		}
		return null;
	}

	public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {
		int count = 0;
		try {
			return super.getChildren(path, watch ? this.watcher : null);
		} catch (KeeperException.ConnectionLossException e) {
			do {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (this.closed) {
					break;
				}
			} while ((this.limit == -1) || (count++ < this.limit));
		}
		return new ArrayList<String>();
	}

	public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
		int count = 0;
		try {
			return super.getChildren(path, watcher);
		} catch (KeeperException.ConnectionLossException e) {
			do {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (this.closed) {
					break;
				}
			} while ((this.limit == -1) || (count++ < this.limit));
		}
		return new ArrayList<String>();
	}

	public byte[] getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
		int count = 0;
		try {
			return super.getData(path, watch ? this.watcher : null, stat);
		} catch (KeeperException.ConnectionLossException e) {
			do {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (this.closed) {
					break;
				}
			} while ((this.limit == -1) || (count++ < this.limit));
		}
		return null;
	}

	public byte[] getData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
		int count = 0;
		try {
			return super.getData(path, watcher, stat);
		} catch (KeeperException.ConnectionLossException e) {
			do {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
				if (this.closed) {
					break;
				}
			} while ((this.limit == -1) || (count++ < this.limit));
		}
		return null;
	}

	public Stat setACL(String path, List<ACL> acl, int version) throws KeeperException, InterruptedException {
		int count = 0;
		do {
			try {
				return super.setACL(path, acl, version);
			} catch (KeeperException.ConnectionLossException e) {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");

				Stat s = exists(path, false);
				if (s != null) {
					if (getACL(path, s).equals(acl)) {
						return s;
					}
				} else
					return null;
			}
		} while ((!this.closed) && ((this.limit == -1) || (count++ < this.limit)));
		return null;
	}

	public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
		int count = 0;
		do {
			try {
				return super.setData(path, data, version);
			} catch (KeeperException.ConnectionLossException e) {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");

				Stat s = exists(path, false);
				if (s != null) {
					if (getData(path, false, s) == data) {
						return s;
					}
				} else
					return null;
			}
		} while ((!this.closed) && ((this.limit == -1) || (count++ < this.limit)));
		return null;
	}

	public void setRetryLimit(int limit) {
		this.limit = limit;
	}

	public boolean testConnection() {
		int count = 0;
		do {
			try {
				return super.exists("/", null) != null;
			} catch (Exception e) {
				logger.warn("zookeeper 连接失败,正在尝试重新连接");
			}
		} while (count++ < 5);
		return false;
	}
}