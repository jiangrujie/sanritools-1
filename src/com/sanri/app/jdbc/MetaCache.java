package com.sanri.app.jdbc;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-6下午2:02:34<br/>
 * 功能:缓存的元数据实例 <br/>
 */
public class MetaCache {
	private String connName;
	private ConnectionInfo connectionInfo;
	// schemaName ==> schema
	private Map<String, Schema> schemaMap = new HashMap<String, Schema>();
	
	public String getConnName() {
		return connName;
	}
	public void setConnName(String connName) {
		this.connName = connName;
	}
	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}
	public void setConnectionInfo(ConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}
	public Map<String, Schema> getSchemaMap() {
		return schemaMap;
	}
	public void setSchemaMap(Map<String, Schema> schemaMap) {
		this.schemaMap = schemaMap;
	}
}
