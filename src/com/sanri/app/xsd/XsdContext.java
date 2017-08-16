package com.sanri.app.xsd;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import sanri.utils.DOMUtil;

/**
 * 
 * 创建时间:2017-8-16上午7:09:32<br/>
 * 创建者:sanri<br/>
 * 功能:xsd 文件解析上下文<br/>
 */
public class XsdContext {
	
	private Log logger = LogFactory.getLog(getClass());

	/**
	 * 通过 dom4j document 构建一个 xsd 上下文
	 * @param document
	 */
	public XsdContext(Document document) {
		parse(document);
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-16下午4:46:21<br/>
	 * 功能:解析文档 <br/>
	 * @param document
	 */
	@SuppressWarnings("unchecked")
	private void parse(Document document) {

		//获取命名空间等属性
		Element rootElement = document.getRootElement();
		Namespace xsdNamespace = rootElement.getNamespaceForURI(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		//初始化简单类型 token 也是一种基本类型,	不包含换行符、回车或制表符、开头或结尾空格或者多个连续空格的字符串
		String [] simpleTypes = {"string","int","long","boolean","token"};
		for (String simpleType : simpleTypes) {
			XsdType xsdType = new XsdType();
			xsdType.setPrimitive(true);
			xsdType.setGlobal(true);
			xsdType.setChildParams(null);
			xsdType.setTypeName(simpleType);
			if("boolean".equals(xsdType)){
				xsdType.setEnumtype(true);
				List<String> values = new ArrayList<String>();
				values.add("true");values.add("false");
				xsdType.setValues(values);
			}
			typeMap.put(simpleType, xsdType);
		}
		
		//解析导入的文档
		Iterator<Element> importElementIterator = rootElement.elementIterator(new QName(IMPORT_NAME, xsdNamespace));
		while(importElementIterator.hasNext()){
			Element importElement = importElementIterator.next();
			String schemaLocation = DOMUtil.elementAttrValue(importElement, "schemaLocation");
			if(StringUtils.isBlank(schemaLocation)){continue;}
			try {
				XsdContext importXsdContext = XsdLoader.loadXsdFromUrl(schemaLocation);
				this.importMap.put(schemaLocation, importXsdContext);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			
		}
		
		//解析所有文档中定义的类型
		Iterator<Element> typeIterator = rootElement.elementIterator(new QName(COMPLEX_TYPE_NAME, xsdNamespace));
		while(typeIterator.hasNext()){
			Element typeElement = typeIterator.next();
			XsdType xsdType = parseTypeElement(typeElement,xsdNamespace);
			typeMap.put(xsdType.getTypeName(), xsdType);
		}
		//解析文档中所有的元素
		Iterator<Element> elementIterator = rootElement.elementIterator(new QName(ELEMENT_NAME,xsdNamespace));
		while(elementIterator.hasNext()){
			Element element = elementIterator.next();
			XsdParam xsdParam = parseElement(element);
			paramMap.put(xsdParam.getParamName(), xsdParam);
		}
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-16下午9:33:19<br/>
	 * 功能:解析 xsd 参数 <br/>
	 * @param element
	 * @return
	 */
	private XsdParam parseElement(Element element) {
		
		return null;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-16上午10:48:16<br/>
	 * 功能:解析类型元素 <br/>
	 * @param typeElement
	 */
	@SuppressWarnings("unchecked")
	private XsdType parseTypeElement(Element typeElement,Namespace xsdNamespace) {
		String nameValue = DOMUtil.elementAttrValue(typeElement, "name");
		XsdType xsdType = new XsdType();
		xsdType.setTypeName(nameValue);
		
		//解析类型下面所有的参数及类型
		List<XsdParam> childParams = new ArrayList<XsdParam>();
		xsdType.setChildParams(childParams );
		
		//获取所有的属性,即子参数类型
		Iterator<Element> typeParamIterator = typeElement.elementIterator(new QName(ATTRIBUTE_NAME, xsdNamespace));
		while(typeParamIterator.hasNext()){
			Element typeParamElement = typeParamIterator.next();
			XsdParam xsdParam = new XsdParam();			//对于每一个属性是一个 xsdparam
			childParams.add(xsdParam);
			//解析名称和类型
			String typeParamName = DOMUtil.elementAttrValue(typeParamElement, "name");
			xsdParam.setParamName(typeParamName);
			
			//检查其类型,如果是简单类型,则直接设置,如果是复杂类型,则需要递归; 如果已经包含了类型,则使用类型(简单类型也是如此)
			String typeValue = DOMUtil.elementAttrValue(typeParamElement, "type");
			if(typeMap.containsKey(typeValue)){
				xsdParam.setParamType(typeMap.get(typeValue));
				logger.debug("已经解析过的类型:"+typeValue);
			}else{
				if(StringUtils.isNotBlank(typeValue)){
					//非空值,使用新的复杂类型,找到此类型并进行解析
					logger.error("这种情况应该不会出现吧,需要递归的");
				}else{
					logger.info("内部复杂元素:"+typeParamName);
					//内部复杂元素,另一种递归,默认只找 simpleType(优先)和 complexType
					XsdType localXsdType = parseLocalComplexType(typeParamElement,xsdNamespace);
					xsdParam.setParamType(localXsdType);
				}
			}
		}
		return xsdType;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-16下午9:12:38<br/>
	 * 功能:内部的复杂元素解析,类型名称使用父级节点名称 注:可能会导致参数重复问题<br/>
	 * @param parentElement 父级元素
	 * @param xsdNamespace 命名空间
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private XsdType parseLocalComplexType(Element parentElement,Namespace xsdNamespace) {
		XsdType xsdType = new XsdType();
		String typeName = DOMUtil.elementAttrValue(parentElement, "name");
		xsdType.setTypeName(typeName);
		xsdType.setGlobal(false);
		
		Element simpleTypeElement = parentElement.element(new QName(SIMPLE_TYPE_NAME,xsdNamespace));
		if(simpleTypeElement != null){
			//现在只找 restriction 类型节点 ,默认是枚举类型
			Element restrictionElement = simpleTypeElement.element(new QName(RESTRICTION_TYPE_NAME,xsdNamespace));
			Iterator<Element> elementIterator = restrictionElement.elementIterator(new QName(ENUMERATION_TYPE_NAME, xsdNamespace));
			xsdType.setPrimitive(true);
			xsdType.setEnumtype(true);
			xsdType.setChildParams(null);
			List<String> values = new ArrayList<String>();
			xsdType.setValues(values );
			
			while(elementIterator.hasNext()){
				Element enumElement = elementIterator.next();
				values.add(DOMUtil.elementAttrValue(enumElement, "value"));
			}
			logger.info("枚举类型"+typeName+":"+values);
		}else{
			logger.info("有复杂类型??"+typeName);
		}
		return null;
	}

	public final static String IMPORT_NAME = "import";
	public final static String COMPLEX_TYPE_NAME = "complexType";
	public final static String ELEMENT_NAME = "element";
	public final static String ATTRIBUTE_NAME = "attribute";
	public final static String SIMPLE_TYPE_NAME = "simpleType";
	public final static String RESTRICTION_TYPE_NAME = "restriction";
	public final static String ENUMERATION_TYPE_NAME = "enumeration";
	
	// xsd 地址,可以是文件协议,http 协议,有可能为空,如果是通过 xml 字符串加载的话
	private URL xsdUrl;
	// 参数列表 参数名=->参数类型
	private Map<String,XsdParam> paramMap = new HashMap<String, XsdParam>();
	// 类型列表 类型名==>类型,如果无类型名,则使用上级参数名
	private Map<String,XsdType> typeMap = new HashMap<String, XsdType>();
	//导入的 xsd 文件列表 uri ==> xsdContext
	private Map<String,XsdContext> importMap = new HashMap<String, XsdContext>();
	public URL getXsdUrl() {
		return xsdUrl;
	}
	void setXsdUrl(URL xsdUrl) {
		this.xsdUrl = xsdUrl;
	}
}
