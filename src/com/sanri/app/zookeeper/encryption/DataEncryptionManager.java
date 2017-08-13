package com.sanri.app.zookeeper.encryption;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-22下午6:04:28<br/>
 * 功能:数据编码方式 <br/>
 */
public interface DataEncryptionManager {
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午6:04:37<br/>
	 * 功能:编码数据 <br/>
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	byte[] encryptData(String paramString) throws Exception;

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午6:04:48<br/>
	 * 功能:解码数据 <br/>
	 * @param paramArrayOfByte
	 * @return
	 * @throws Exception
	 */
	String decryptData(byte[] paramArrayOfByte) throws Exception;
}