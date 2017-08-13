package com.sanri.app.jdbc;

import java.util.List;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-6下午12:47:37<br/>
 * 功能:instance  对于 mysql 为 datebase,对于 oracle 为 schema 默认使用用户名  <br/>
 */
public class Schema {
	private String instance;
	private List<Table> tables;
	
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public List<Table> getTables() {
		return tables;
	}
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
}
