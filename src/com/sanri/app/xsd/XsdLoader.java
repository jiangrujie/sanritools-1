package com.sanri.app.xsd;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-8-16上午9:52:00<br/>
 * 功能:xsd 加载器 <br/>
 */
public class XsdLoader {
	private XsdLoader(){}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-16上午9:58:22<br/>
	 * 功能: 从文件加载 xsd 定义文件进行解析<br/>
	 * @param file
	 * @return
	 * @throws FileNotFoundException 
	 * @throws DocumentException 
	 * @throws MalformedURLException 
	 */
	public static XsdContext loadXsdFromFile(File file) throws FileNotFoundException, DocumentException{
		if(!file.exists()){
			throw new FileNotFoundException("文件未找到:"+file);
		}
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		XsdContext xsdContext = new XsdContext(document);
		return xsdContext;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-16上午9:59:02<br/>
	 * 功能:从网络路径加载 xsd 定义文件 <br/>
	 * @param url
	 * @return
	 * @throws MalformedURLException 
	 * @throws DocumentException 
	 */
	public static XsdContext loadXsdFromUrl(String url) throws MalformedURLException, DocumentException{
		URL realUrl = new URL(url);
		return loadXsd(realUrl);
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-16上午10:12:08<br/>
	 * 功能:从 xsd 字符串加载 <br/>
	 * @param xsdString
	 * @return
	 * @throws DocumentException
	 */
	public static XsdContext loadXsdFromString(String xsdString) throws DocumentException{
		Document document = DocumentHelper.parseText(xsdString);
		XsdContext xsdContext = new XsdContext(document);
		return xsdContext;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-16上午10:02:46<br/>
	 * 功能:从 url 加载 xsd 定义文件 <br/>
	 * @param url
	 * @return
	 * @throws DocumentException 
	 */
	public static XsdContext loadXsd(URL url) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(url);
		XsdContext xsdContext = new XsdContext(document);
		xsdContext.setXsdUrl(url);
		return xsdContext;
	}
}
