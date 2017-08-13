package com.sanri.app.zookeeper;

/**
 * 
 * 创建时间:2017-7-23上午7:12:32<br/>
 * 创建者:sanri<br/>
 * 功能:zookeeper 连接配置<br/>
 */
public class ZooConfig {
	private String name;
	private String host;
	private int port;
	private String encryptionManager;
	private String authScheme;
	private String authData;
	private int timeout = 5000;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getEncryptionManager() {
		return encryptionManager;
	}
	public void setEncryptionManager(String encryptionManager) {
		this.encryptionManager = encryptionManager;
	}
	public String getAuthScheme() {
		return authScheme;
	}
	public void setAuthScheme(String authScheme) {
		this.authScheme = authScheme;
	}
	public String getAuthData() {
		return authData;
	}
	public void setAuthData(String authData) {
		this.authData = authData;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
