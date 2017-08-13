package com.sanri.app.jdbc;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-6下午2:02:25<br/>
 * 功能:连接信息 <br/>
 */
public class ConnectionInfo {
	private String name;
	private String host;
	private String port;
	private String username;
	private String userpass;
	private String dbType;
	private String database;				//这里对于 mysql 连接数据库,对于 oracle 为实例名
	private String spellingRule="lower";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserpass() {
		return userpass;
	}
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getSpellingRule() {
		return spellingRule;
	}
	public void setSpellingRule(String spellingRule) {
		this.spellingRule = spellingRule;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
}
