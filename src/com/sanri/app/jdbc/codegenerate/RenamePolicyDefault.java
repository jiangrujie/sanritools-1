package com.sanri.app.jdbc.codegenerate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * 作者:sanri</br> 
 * 时间:2016-9-26下午2:04:07</br> 
 * 功能:默认命名策略<br/>
 */
public class RenamePolicyDefault implements RenamePolicy {
	private Map<String,String> typeMirror = new HashMap<String, String>();
	
	public RenamePolicyDefault(Map<String,String> typeMirror){
		this.typeMirror = typeMirror;
	}
	
	@Override
	public String mapperClassName(String tableName) {
		if (!StringUtils.isBlank(tableName)) {
			tableName = tableName.toLowerCase();
			String[] part = tableName.split("_");
			String className = "";
			for (int i = 0; i < part.length; i++) {
				className += StringUtils.capitalize(part[i]);
			}
			return className;
		}
		return tableName;
	}

	@Override
	public String mapperPropertyName(String columnName) {
		if (!StringUtils.isBlank(columnName)) {
			columnName = columnName.toLowerCase();
			String[] part = columnName.split("_");
			String newColumnName = part[0];
			for (int i = 1; i < part.length; i++) {
				newColumnName += StringUtils.capitalize(part[i]);
			}
			return newColumnName;
		}
		return columnName;
	}

	@Override
	public String mapperPropertyType(String columnType) {
		return typeMirror.get(columnType);
	}
}