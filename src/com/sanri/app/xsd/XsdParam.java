package com.sanri.app.xsd;


/**
 * 
 * 创建时间:2017-8-16上午7:19:48<br/>
 * 创建者:sanri<br/>
 * 功能:xsd 元素<br/>
 */
public class XsdParam {
	private String paramName;
	private XsdType paramType;
	//是否是数组
	private boolean array;
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public XsdType getParamType() {
		return paramType;
	}
	public void setParamType(XsdType paramType) {
		this.paramType = paramType;
	}
	public boolean isArray() {
		return array;
	}
	public void setArray(boolean array) {
		this.array = array;
	}
}
