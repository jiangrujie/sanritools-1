package com.sanri.app.xsd;

import java.util.List;


/**
 * 
 * 创建时间:2017-8-16上午7:17:13<br/>
 * 创建者:sanri<br/>
 * 功能:xsd 的数据类型<br/>
 */
public class XsdType {
	private boolean global;		//是否全局类型
	private boolean primitive;	//是否原始类型
	private boolean enumtype;	//是否是枚举类型
	private List<XsdType> values;	//枚举类型可能的值
	private String typeName;
	private List<XsdParam> childParams;
	
	public boolean isGlobal() {
		return global;
	}
	public void setGlobal(boolean global) {
		this.global = global;
	}
	public boolean isPrimitive() {
		return primitive;
	}
	public void setPrimitive(boolean primitive) {
		this.primitive = primitive;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public List<XsdParam> getChildParams() {
		return childParams;
	}
	public void setChildParams(List<XsdParam> childParams) {
		this.childParams = childParams;
	}
	public boolean isEnumtype() {
		return enumtype;
	}
	public void setEnumtype(boolean enumtype) {
		this.enumtype = enumtype;
	}
	public List<XsdType> getValues() {
		return values;
	}
	public void setValues(List<XsdType> values) {
		this.values = values;
	}
}
