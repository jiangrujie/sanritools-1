package com.sanri.app.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import sanri.utils.PathUtil;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-4-20上午10:57:58<br/>
 * 功能:jdbc 元数据管理 <br/>
 */
public class MetaManager {
	// 连接名称 ==> 连接信息
	private static Map<String, ConnectionInfo> connections = new HashMap<String, ConnectionInfo>();
	// 连接名称 == > 所有用户表信息
	private static Map<String,MetaCache> metaCacheMap = new HashMap<String, MetaCache>();
	
	static{
		//读取默认 jdbc 配置
		String defaultConnectionConfigPath = PathUtil.pkgPath("com.sanri.config");
		try {
			PropertiesConfiguration properties = new PropertiesConfiguration(defaultConnectionConfigPath+"/jdbcdefault.properties");
			String dbType = properties.getString("dbType");
			String spellingRule = properties.getString("spellingRule");
			String host = properties.getString("host");
			int port = properties.getInt("port");
			String username = properties.getString("username");
			String userpass = properties.getString("userpass");
			String database = properties.getString("database");
			
			//新建连接信息
			ConnectionInfo connectionInfo = new ConnectionInfo();
			connectionInfo.setName("default");
			connectionInfo.setDbType(dbType);
			connectionInfo.setSpellingRule(spellingRule);
			connectionInfo.setHost(host);
			connectionInfo.setPort(String.valueOf(port));
			connectionInfo.setUsername(username);
			connectionInfo.setUserpass(userpass);
			connectionInfo.setDatabase(database);
			
			saveConnection(connectionInfo.getName(), connectionInfo);
			loadMeta(connectionInfo);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveConnection(String name,ConnectionInfo connectionInfo){
		connections.put(name, connectionInfo);
	}
	public static boolean existConnection(String name){
		return connections.containsKey(name);
	}
	public static ConnectionInfo getConnectionInfo(String name){
		return connections.get(name);
	}
	public static Set<String> connections(){
		return connections.keySet();
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-6下午2:28:57<br/>
	 * 功能: 对于一个连接进行元数据的加载 <br/>
	 * @param connectionInfo
	 */
	public static MetaCache loadMeta(ConnectionInfo connectionInfo) {
		String connName = connectionInfo.getName();
		if(metaCacheMap.containsKey(connName)){
			return metaCacheMap.get(connName);
		}
		MetaCache metaCache = new MetaCache();
		metaCache.setConnectionInfo(connectionInfo);
		metaCache.setConnName(connName);
		metaCacheMap.put(connName, metaCache);
		//加载默认的 schema 
		String dbType = connectionInfo.getDbType();
		if("mysql".equalsIgnoreCase(dbType)){
			loadMysqlMeta(connectionInfo,metaCache);
		}else if("oracle".equalsIgnoreCase(dbType)){
			loadOracleMeta(connectionInfo,metaCache);
		}
		return metaCache;
	}
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-6下午3:50:57<br/>
	 * 功能:加载 mysql 的所有表信息 <br/>
	 * @param connection
	 * @param connectionInfo
	 * @param database
	 */
	public static Schema loadMysqlTables( ConnectionInfo connectionInfo,String schemaName) {
		Schema schemaDb = new Schema();
		schemaDb.setInstance(schemaName);
		
		Connection connection = connection(connectionInfo,schemaName);
		String spellingRule = connectionInfo.getSpellingRule();
		Map<String,String> commentsMap = new HashMap<String, String>();
		List<Table> tables = new ArrayList<Table>();
		schemaDb.setTables(tables);
		try {
			PreparedStatement prepareStatement = connection.prepareStatement("show table status");
			ResultSet resultSet = prepareStatement.executeQuery();
			while(resultSet.next()){
				String tableName = ObjectUtils.toString(resultSet.getString("name")).toLowerCase();
				String comments = resultSet.getString("comment");
				if("upper".equals(spellingRule)){
					tableName = tableName.toUpperCase();
				}
				commentsMap.put(tableName, comments);
			}
			//schema 
			String schema = connectionInfo.getUsername().toUpperCase();
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet tablesResultSet = metaData.getTables(null, schema, null, new String[] { "TABLE" });
			ResultSet columnsResultSet = metaData.getColumns(null, schema, null, null);
			
			//遍历所有的列,形成 map 供 table 设置进入
			Map<String,List<Column>> columnMap = findColumnMap(columnsResultSet, spellingRule);
			//获取所有表
			while(tablesResultSet.next()){
				Table table = new Table();
				String tableName = tablesResultSet.getString("TABLE_NAME");		//表名
				if("upper".equals(spellingRule)){
					table.setTableName(tableName.toUpperCase());
				}else{
					table.setTableName(tableName.toLowerCase());
				}
				
				String tableComments = commentsMap.get(tableName);
				table.setComments(tableComments);
				table.setColumns(columnMap.get(table.getTableName()));
				tables.add(table);
			}
			tablesResultSet.close();
			columnsResultSet.close();
			resultSet.close();
			prepareStatement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schemaDb;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-6下午6:28:47<br/>
	 * 功能:获取更改数据库的连接 <br/>
	 * @param connectionInfo
	 * @param schemaName
	 * @return
	 */
	public static Connection connection(ConnectionInfo connectionInfo, String schemaName) {
		try {
			ConnectionInfo newConn = new ConnectionInfo();
			BeanUtils.copyProperties(newConn, connectionInfo);
			newConn.setDatabase(schemaName);
			return connection(newConn);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-24下午2:12:38<br/>
	 * 功能:根据表名获取需要的表 <br/>
	 * 入参: <br/>
	 */
	public static List<Table> tables(String connName,String database,List<String> tables){
		ConnectionInfo connectionInfo = connections.get(connName);
		MetaCache loadMeta = loadMeta(connectionInfo);
		Map<String, Schema> schemaMap = loadMeta.getSchemaMap();
		Schema schema = schemaMap.get(database);
		if(schema == null || schema.getTables() == null){
			schema = loadMysqlTables(connectionInfo, database);
			schemaMap.put(database, schema);
		}
		List<Table> allTables = schema.getTables();
		List<Table> needTables = new ArrayList<Table>();
		for (Table table : allTables) {
			String tableName = table.getTableName();
			if(tables.contains(tableName)){
				needTables.add(table);
			}
		}
		return needTables;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21下午4:29:21<br/>
	 * 功能:获取单张表配置 <br/>
	 * 入参: <br/>
	 */
	public static Table table(String connName,String database,String tableName){
		List<String> needTableNames = new ArrayList<String>();
		needTableNames.add(tableName);
		List<Table> tables = tables(connName, database, needTableNames);
		if(tables != null && tables.size() > 0){
			return tables.get(0);
		}
		return null;
	}
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-6下午3:51:53<br/>
	 * 功能: 加载 oracle 元数据<br/>
	 * @param connectionInfo
	 * @param metaCache
	 */
	private static void loadOracleMeta(ConnectionInfo connectionInfo, MetaCache metaCache) {
		ResultSet tablesResultSet = null;
		ResultSet columnsResultSet = null;
		List<Table> tables = new ArrayList<Table>();
		String database = connectionInfo.getDatabase();
		Schema schemaDB = new Schema();
		schemaDB.setInstance(database);
		schemaDB.setTables(tables);
		metaCache.getSchemaMap().put(connectionInfo.getDatabase(), schemaDB);
		
		Connection connection = connection(connectionInfo);
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			//schema 
			String schema = connectionInfo.getUsername().toUpperCase();
			tablesResultSet = metaData.getTables(null, schema, null, new String[] { "TABLE" });
			columnsResultSet = metaData.getColumns(null, schema, null, null);
			
			String spellingRule = connectionInfo.getSpellingRule();
			//遍历所有的列,形成 map 供 table 设置进入
			Map<String,List<Column>> columnMap = findColumnMap(columnsResultSet, spellingRule);
			
			//遍历所有的表,加入缓存 
			while(tablesResultSet.next()){
				Table table = new Table();
				String tableName = tablesResultSet.getString("TABLE_NAME");		//表名
				if("upper".equals(spellingRule)){
					table.setTableName(tableName.toUpperCase());
				}else{
					table.setTableName(tableName.toLowerCase());
				}
				String tableComments = tablesResultSet.getString("REMARKS");		//注释 mysql 这样获取不到注释  
				table.setComments(tableComments);
				table.setColumns(columnMap.get(table.getTableName()));
				tables.add(table);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				tablesResultSet.close();
				columnsResultSet.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-6下午2:44:16<br/>
	 * 功能: 加载 mysql 元数据 <br/>
	 * @param connectionInfo 连接信息(需包含默认数据库)
	 * @param metaCache
	 */
	private static void loadMysqlMeta(ConnectionInfo connectionInfo, MetaCache metaCache) {
		Connection connection = connection(connectionInfo);
		QueryRunner queryRunner = new QueryRunner();
		try {
			List<String> databases = queryRunner.query(connection, "show databases ",new ResultSetHandler<List<String>>(){
				@Override
				public List<String> handle(ResultSet resultSet) throws SQLException {
					List<String> databases = new ArrayList<String>();
					while(resultSet.next()){
						String database = resultSet.getString(1);
						databases.add(database);
					}
					return databases;
				}
			});
			//对查询到的数据库列表循环, mysql 一定会有一个库的,这里不需要担心为空的问题
			for (String database : databases) {
				Schema schema = new Schema();
				schema.setInstance(database);
				if(database.equals(connectionInfo.getDatabase())){
					schema = loadMysqlTables(connectionInfo,database);
				}
				metaCache.getSchemaMap().put(database, schema);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DbUtils.closeQuietly(connection);
		}
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-20下午2:49:10<br/>
	 * 功能:查找表格列映射 <br/>
	 * 入参: <br/>
	 */
	private static Map<String, List<Column>> findColumnMap(ResultSet columnsResultSet, String spellingRule) throws SQLException {
		Map<String, List<Column>> columnMap = new HashMap<String, List<Column>>();
		while(columnsResultSet.next()){
			String tableName = columnsResultSet.getString("TABLE_NAME");
			String columnName = columnsResultSet.getString("COLUMN_NAME");		//列名
			String columnType = columnsResultSet.getString("TYPE_NAME");		//类型名
			String columnComment = columnsResultSet.getString("REMARKS");		//备注
			int length = columnsResultSet.getInt("COLUMN_SIZE");				//长度
			int precision = columnsResultSet.getInt("DECIMAL_DIGITS");			//精度
			String nullable = columnsResultSet.getString("IS_NULLABLE");		//是否可以空
			boolean nullableBool = "no".equalsIgnoreCase(nullable) ? false : true;
			
			List<Column> columns = null;
			if("upper".equals(spellingRule)){
				columns = columnMap.get(tableName.toUpperCase());
				if(columns == null){
					columns = new ArrayList<Column>();
					columnMap.put(tableName.toUpperCase(), columns);
				}
			}else{
				columns = columnMap.get(tableName.toLowerCase());
				if(columns == null){
					columns = new ArrayList<Column>();
					columnMap.put(tableName.toLowerCase(), columns);
				}
			}
			
			Column column = new Column();
			columns.add(column);
			column.setLength(length);
			column.setPrecision(precision);
			column.setNullable(nullableBool);
			column.setComments(columnComment);
			if("upper".equals(spellingRule)){
				column.setColumnName(columnName.toUpperCase());
				column.setDataType(columnType.toUpperCase());
				continue;
			}
			column.setColumnName(columnName.toLowerCase());
			column.setDataType(columnType.toLowerCase());
		}
		return columnMap;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-6下午3:50:04<br/>
	 * 功能:获取连接信息 <br/>
	 * @param connectionInfo
	 * @return
	 */
	public static Connection connection(ConnectionInfo connectionInfo){
		String dbType = connectionInfo.getDbType();
		String instance = connectionInfo.getDatabase();
		if(!StringUtils.isBlank(dbType)){
			String url = "";
			try {
				if("mysql".equalsIgnoreCase(dbType)){
					url = "jdbc:mysql://"+connectionInfo.getHost()+":"+connectionInfo.getPort()+"/"+instance+"?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
					Class.forName("com.mysql.jdbc.Driver");
				}else if("oracle".equalsIgnoreCase(dbType)){
					url = "jdbc:oracle:thin:@"+connectionInfo.getHost()+":"+connectionInfo.getPort()+instance;
					Class.forName("oracle.jdbc.driver.OracleDriver");
				}
				Connection connection = DriverManager.getConnection(url, connectionInfo.getUsername(),connectionInfo.getUserpass());
				return connection;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
		return null;
	}
}
