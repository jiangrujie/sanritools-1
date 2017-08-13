package com.sanri.app.jdbc.codegenerate;

import static com.sanri.app.jdbc.codegenerate.JavaBean.BODY_BEGIN;
import static com.sanri.app.jdbc.codegenerate.JavaBean.BODY_END;
import static com.sanri.app.jdbc.codegenerate.JavaBean.COMMENTS_LINE;
import static com.sanri.app.jdbc.codegenerate.JavaBean.COMMENTS_MULTI_LINE_BEGIN;
import static com.sanri.app.jdbc.codegenerate.JavaBean.COMMENTS_MULTI_LINE_END;
import static com.sanri.app.jdbc.codegenerate.JavaBean.N;
import static com.sanri.app.jdbc.codegenerate.JavaBean.PLACEHOLDER;
import static com.sanri.app.jdbc.codegenerate.JavaBean.STATEMENT_END;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import sanri.utils.Validate;

import com.sanri.app.jdbc.Column;
import com.sanri.app.jdbc.Table;

/**
 * 
 * 创建时间:2017-7-8下午5:33:21<br/>
 * 创建者:sanri<br/>
 * 功能:java bean 代码构建<br/>
 * 注:传入类型时,如果不是 java.lang 类型或基本类型时,需完整类名
 */
public class JavaPojo  {
	// 生成模式 hibernate/normal ; hibernate 模式会加入注解
	private String model = "normal";
	//包名,类名
	private String packageName;
	private String className;
	private String classComment;
	//继承
	private String extendsName;
	//需要排除的列
	private List<String> excludeColumns = new ArrayList<String>();
	//实现的接口列表
	private List<String> interfaces = new ArrayList<String>();
	private Table table;
	private Map<String,Column> propertyColumns = new HashMap<String, Column>();
	private RenamePolicy renamePolicy = null;
	
	//使用 persistent api 时需要导入的几个注解
	private final static List<String> HIBERNATE_IMPORT = new ArrayList<String>();
	static{
		HIBERNATE_IMPORT.add("javax.persistence.Entity");
		HIBERNATE_IMPORT.add("javax.persistence.Table");
		HIBERNATE_IMPORT.add("javax.persistence.Id");
		HIBERNATE_IMPORT.add("javax.persistence.GenerationType");
		HIBERNATE_IMPORT.add("javax.persistence.GeneratedValue");
		HIBERNATE_IMPORT.add("javax.persistence.Column");
	}
	
	/**
	 * 普通模式和 hibernate 模式
	 */
	public final static String MODEL_HIBERNATE = "hibernate";
	public final static String MODEL_NORMAL = "normal";
	
	/**
	 * 通过表构建一个 java 实体
	 * @param model
	 * @param packageName
	 * @param table
	 * @param renamePolicy 
	 */
	public JavaPojo(String model,String packageName,Table table, RenamePolicy renamePolicy){
		this.model = model;
		this.packageName = packageName;
		this.table = table;
		this.renamePolicy = renamePolicy;
		if(table == null){
			throw new IllegalArgumentException("需传入数据源");
		}
		//获取类名
		className = table.getTableName();
		if(renamePolicy != null){
			className = renamePolicy.mapperClassName(table.getTableName());
		}
		//获取类注释 
		classComment = table.getComments();
		if(StringUtils.isBlank(classComment)){
			classComment = "generate from table @"+table.getTableName();
		}
		//获取属性列
		List<Column> columns = table.getColumns();
		if(!Validate.isEmpty(columns)){
			for (Column column : columns) {
				String propertyName = column.getColumnName();
				if(renamePolicy != null){
					propertyName = renamePolicy.mapperPropertyName(propertyName);
				}
				propertyColumns.put(propertyName, column);
			}
		}
	}
	
	/**
	 * 
	 * 功能:构建 java bean <br/>
	 * 创建时间:2017-7-8下午5:46:43<br/>
	 * 作者：sanri<br/>
	 * @return<br/>
	 */
	public List<String> build(){
		List<String> classCode = new ArrayList<String>();
		
		Set<String> headCode = buildHead();
		//构建主体
		List<String> bodyCode = new ArrayList<String>();
		//先加入注释 
		if(!StringUtils.isBlank(classComment)){
			bodyCode.add(COMMENTS_MULTI_LINE_BEGIN);
			bodyCode.add(" * "+classComment);
			bodyCode.add(COMMENTS_MULTI_LINE_END);
		}
		if(MODEL_HIBERNATE.equals(model)){
			//hibernate 模式下,需加入 persistant 注解
			bodyCode.add("@Entity"); 
			bodyCode.add("@Table(name=\""+table.getTableName()+"\")");
		}
		//编写首行代码
		StringBuffer firstLine = new StringBuffer("public class "+className);
		if(!StringUtils.isBlank(extendsName)){
			String extendsBeanName = extendsName;
			if(extendsName.indexOf(".") != -1){
				extendsBeanName = extendsName.substring(extendsName.lastIndexOf(".") + 1);
			}
			firstLine.append(" extends "+extendsBeanName);
		}
		if(interfaces != null && interfaces.size() > 0){
			firstLine.append(" implements ");
			for (int i=0;i<interfaces.size();i++) {
				String inter = interfaces.get(i);
				if(inter.indexOf(".") != -1){
					inter = inter.substring(inter.lastIndexOf(".") + 1);
				}
				firstLine.append(inter);
				if(i != interfaces.size() - 1 ){
					firstLine.append(",");
				}
				
			}
		}
		bodyCode.add(firstLine.toString()+BODY_BEGIN);
		
		List<String> methodCode = new ArrayList<String>();		//带入 methodCode
		List<String> propertyCode = buildProperty(headCode,methodCode);
		bodyCode.addAll(propertyCode);
		bodyCode.addAll(methodCode);
		bodyCode.add(BODY_END);
		
		classCode.addAll(headCode);
		classCode.addAll(bodyCode);
		return classCode;
	}
	
	/**
	 * 功能:将代码列表写出到文件<br/>
	 * 创建时间:2016-9-25下午2:33:27<br/>
	 * 作者：sanri<br/>
	 * 入参说明:代码列表<br/>
	 * 出参说明：写出的文件<br/>
	 */
	public File writerBean(List<String> javaClass,File outputDir){
		File javaFile = new File(outputDir, className+".java");
		FileWriter fr = null;
		try {
			fr = new FileWriter(javaFile); 
			IOUtils.writeLines(javaClass, N, fr);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(fr);
		}
		
		return javaFile;
	}
	
	/**
	 * 
	 * 功能:构建属性代码<br/>
	 * 创建时间:2017-7-9上午6:30:14<br/>
	 * 作者：sanri<br/>
	 * @param headCode 
	 * @param methodCode 
	 * @return<br/>
	 */
	private List<String> buildProperty(Set<String> headCode, List<String> methodCode) {
		List<String> fields = new ArrayList<String>();
		if(propertyColumns != null && !propertyColumns.isEmpty()){
			Iterator<String> propertyIterator = propertyColumns.keySet().iterator();
			while(propertyIterator.hasNext()){
				String propertyName = propertyIterator.next();
				//如果是需要排除的列,不生成代码
				Column column = propertyColumns.get(propertyName);
				//去掉排除列(数据库列名)
				if(!Validate.isEmpty(excludeColumns) && excludeColumns.contains(column.getColumnName())){continue;}
				String propertyType = "String";
				String propertyComment = column.getComments();
				if(renamePolicy != null){
					propertyType = renamePolicy.mapperPropertyType(column.getDataType());
					if(StringUtils.isBlank(propertyType)){		//如果没有映射到类型,默认使用 String 类型
						propertyType = "String";
					}
				}
				
				//加入属性注释 
				if(!StringUtils.isBlank(propertyComment)){
					fields.add(PLACEHOLDER+COMMENTS_LINE+" "+propertyComment);
				}
				if(MODEL_HIBERNATE.equals(model)){
					//hibernate 模式加入注解
					fields.add(PLACEHOLDER+"@Column(name=\""+column.getColumnName()+"\",length="+column.getLength()+")");
				}
				//类型的引入 
				String simpleTypeName = propertyType;
				if(propertyType.indexOf(".") != -1){
					headCode.add("import "+propertyType+STATEMENT_END);
					simpleTypeName = propertyType.substring(propertyType.lastIndexOf(".") + 1);
				}
				fields.add(PLACEHOLDER+"private "+simpleTypeName+ " "+propertyName+STATEMENT_END);
				buildSetGet(methodCode,propertyName,simpleTypeName);
			}
		}
		return fields;
	}

	/**
	 * 
	 * 功能:构建 set & get 方法<br/>
	 * 创建时间:2017-7-9上午7:25:31<br/>
	 * 作者：sanri<br/>
	 * @param methodCode 方法代码
	 * @param propertyName 属性名称
	 * @param simpleTypeName 属性类型简单名称<br/>
	 */
	private void buildSetGet(List<String> methodCode, String propertyName,String simpleTypeName) {
		methodCode.add(PLACEHOLDER+COMMENTS_LINE+" setGet "+propertyName);
		//set method
		methodCode.add(PLACEHOLDER+"public void set"+StringUtils.capitalize(propertyName)+"("+simpleTypeName+" "+propertyName+"){");
		methodCode.add(PLACEHOLDER+PLACEHOLDER+"this."+propertyName+" = "+propertyName+STATEMENT_END);
		methodCode.add(PLACEHOLDER+"}");
		//get method
		methodCode.add(PLACEHOLDER+"public "+simpleTypeName+" get"+StringUtils.capitalize(propertyName)+"(){");
		methodCode.add(PLACEHOLDER+PLACEHOLDER+"return this."+propertyName+STATEMENT_END);
		methodCode.add(PLACEHOLDER+"}");
	}

	/**
	 * 
	 * 功能:构建头部代码,包含包信息和导入信息<br/>
	 * 创建时间:2017-7-8下午5:47:23<br/>
	 * 作者：sanri<br/>
	 * @return<br/>
	 */
	private Set<String> buildHead() {
		Set<String> headList = new LinkedHashSet<String>();
		//如果类有包名,则加入包
		if(StringUtils.isNotBlank(packageName)){
			headList.add("package "+packageName+STATEMENT_END+N);
		}
		//导入接口中的包,目前只要导入 java.io.Serializable
		if(interfaces != null && interfaces.size() > 0){
			for (String inter : interfaces) {
				if(inter.indexOf(".") != -1){
					headList.add("import "+inter+STATEMENT_END+N);
				}
			}
		}
		//导入父类 
		if(StringUtils.isNotBlank(extendsName)){
			headList.add("import "+extendsName+STATEMENT_END+N);
		}
		//hibernate 模式导入 persistence api
		if(MODEL_HIBERNATE.equals(model)){
			for (String persistence : HIBERNATE_IMPORT) {
				headList.add("import "+persistence+STATEMENT_END+N);
			}
		}
		return headList;
	}
	
	public void addInterface(String interfaceName){
		this.interfaces.add(interfaceName);
	}
	
	public void addExcludeColumn(String excludeColumn){
		this.excludeColumns.add(excludeColumn);
	}

	/**
	 * set & get 
	 */
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getExtendsName() {
		return extendsName;
	}

	public void setExtendsName(String extendsName) {
		this.extendsName = extendsName;
	}

	public void setExcludeColumns(List<String> excludeColumns) {
		this.excludeColumns = excludeColumns;
	}
	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}

	public void setRenamePolicy(RenamePolicy renamePolicy) {
		this.renamePolicy = renamePolicy;
	}

	public String getClassName() {
		return className;
	}
	
}