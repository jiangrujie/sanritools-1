package com.sanri.app.zookeeper.encryption;

/**
 * 
 * 创建时间:2017-7-22下午8:52:14<br/>
 * 创建者:sanri<br/>
 * 功能:默认节点数据编码实现,直接获取字节码<br/>
 */
public class BasicDataEncryptionManager implements DataEncryptionManager {
	public String decryptData(byte[] encrypted) throws Exception {
		if(encrypted == null){
			return "";
		}
		return new String(encrypted,"utf-8");
	}

	public byte[] encryptData(String data) throws Exception {
		if (data == null) {
			return new byte[0];
		}
		return data.getBytes("utf-8");
	}
}