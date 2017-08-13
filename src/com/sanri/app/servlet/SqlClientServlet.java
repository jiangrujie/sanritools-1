package com.sanri.app.servlet;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import sanri.utils.PathUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sanri.app.BaseServlet;
import com.sanri.app.jdbc.ConnectionInfo;
import com.sanri.app.jdbc.MetaCache;
import com.sanri.app.jdbc.MetaManager;
import com.sanri.app.jdbc.Schema;
import com.sanri.app.jdbc.Table;
import com.sanri.app.jdbc.datatransfer.DataTransfer;
import com.sanri.app.jdbc.sqlfile.FileInfo;
import com.sanri.app.jdbc.sqlfile.FileListResult;
import com.sanri.app.task.BigDataWriteThread;
import com.sanri.frame.RequestMapping;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-4-17下午2:04:05<br/>
 * 功能:sql 客户端功能 <br/>
 */
@RequestMapping("/sqlclient")
public class SqlClientServlet extends BaseServlet{
	private static File sqlBaseDir = null;
	
	static{
		sqlBaseDir = new File(PathUtil.webAppsPath()+"/sql");
		if(!sqlBaseDir.exists()){
			sqlBaseDir.mkdir();
		}
	}
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21上午11:23:25<br/>
	 * 功能:创建连接 <br/>
	 * 入参: <br/>
	 */
	public int createConnection(ConnectionInfo connectionInfo){
		String name = connectionInfo.getName();
		if(MetaManager.existConnection(name)){
			return -1;
		}
		MetaManager.saveConnection(name, connectionInfo);
		return 0;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21上午11:23:14<br/>
	 * 功能:检测是否存在连接名 <br/>
	 * 入参: <br/>
	 */
	public boolean existConnectionName(String name){
		return MetaManager.existConnection(name);
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21上午11:23:04<br/>
	 * 功能:查询所有的连接 <br/>
	 * 入参: <br/>
	 */
	public Set<String> connections(){
		return MetaManager.connections();
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21上午11:23:33<br/>
	 * 功能: 查询连接所有的表<br/>
	 * 入参: 只需要传连接名 <br/>
	 */
	public List<Table> tables(String connName,String database){
		ConnectionInfo connectionInfo = MetaManager.getConnectionInfo(connName);
		MetaCache loadMeta = MetaManager.loadMeta(connectionInfo);
		Map<String, Schema> schemaMap = loadMeta.getSchemaMap();
		Schema schema = schemaMap.get(database);
		List<Table> tables = schema.getTables();
		return tables;
	}
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21下午12:22:08<br/>
	 * 功能: 查询数据库列表 <br/>
	 * 入参: 连接名<br/>
	 * @return 
	 */
	public Collection<Schema> schemas(String connName){
		ConnectionInfo connectionInfo = MetaManager.getConnectionInfo(connName);
		MetaCache loadMeta = MetaManager.loadMeta(connectionInfo);
		Map<String, Schema> schemaMap = loadMeta.getSchemaMap();
		return schemaMap.values();
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-6下午5:27:54<br/>
	 * 功能: <br/>
	 * @param connName 连接名
	 * @param schemaName 数据库名
	 * @return
	 */
	public Schema loadSchema(String connName,String schemaName){
		ConnectionInfo connectionInfo = MetaManager.getConnectionInfo(connName);
		MetaCache loadMeta = MetaManager.loadMeta(connectionInfo);
		Map<String, Schema> schemaMap = loadMeta.getSchemaMap();
		Schema schema = schemaMap.get(schemaName);
		if(schema != null && schema.getTables() != null){
			return schema;
		}
		Schema loadMysqlTables = MetaManager.loadMysqlTables(connectionInfo, schemaName);
		return loadMysqlTables;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-5-27上午10:44:12<br/>
	 * 功能:得到连接信息 <br/>
	 * @param name
	 * @return
	 */
	public ConnectionInfo connectionInfo(String name){
		ConnectionInfo connectionInfo = MetaManager.getConnectionInfo(name);
		return connectionInfo;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21上午11:24:03<br/>
	 * 功能:执行 sql 语句 <br/>
	 * 入参: <br/>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String,Object> executeSql(String connName,String database,String [] executorSqlArray){
		Map<String,Object> result = new HashMap<String, Object>();
		
		ConnectionInfo connectionInfo = MetaManager.getConnectionInfo(connName);
		Connection connection  = null;PreparedStatement ps = null;ResultSet rs = null;
		try {
			if(executorSqlArray != null && executorSqlArray.length > 0){
				QueryRunner queryRunner = new QueryRunner();
				connection = MetaManager.connection(connectionInfo,database);
				
				//循环执行 sql 
				for (String sql : executorSqlArray) {
					if(StringUtils.isBlank(sql) || StringUtils.isBlank(sql.trim())){
						continue;
					}
					try {
						sql = sql.trim().toUpperCase();
						if(sql.startsWith("SELECT")){
							//查询语句
							ps = connection.prepareStatement(sql);
							rs = ps.executeQuery();
							//获取元数据,得到头信息
							ResultSetMetaData metaData = rs.getMetaData();
							int columnCount = metaData.getColumnCount();
							JSONArray head = new JSONArray();
							for (int i = 1; i <= columnCount; i++) {
								head.add(metaData.getColumnName(i));
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("head", head);
							//获取数据信息
							List<Map> data = new ArrayList<Map>();
							while(rs.next()){
								Map dataObj = new HashMap();
								for (int i = 1; i <= columnCount; i++) {
									dataObj.put(metaData.getColumnName(i), rs.getObject(i));
								}
								data.add(dataObj);
							}
							jsonObject.put("body", data);
							result.put(sql, jsonObject);
						}else{
							//修改语句
							int update = queryRunner.update(connection, sql);
							result.put(sql, update);
						}
					} catch (SQLException e) {
						logger.error("sql 执行出错,sql:"+sql);
						e.printStackTrace();
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtils.closeQuietly(connection, ps, rs);
		}
		return result;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-13下午3:30:45<br/>
	 * 功能:往数据库中添加数据 <br/>
	 * @param connName 连接名称
	 * @param database 数据库名称
	 * @param tableName 表名
	 * @param dataMap 数据集,使用列名==>列值的形式添加数据(列需要包含所有列,没数据留空),对于 id 是自动生成的,不要加 id 
	 * @return
	 */
	public String writeData(String connName,String database,String tableName,Map<String,String> dataMap){
		Table table = MetaManager.table(connName, database, tableName);
		if(table == null){
			return "";
		}
		ConnectionInfo connectionInfo = MetaManager.getConnectionInfo(connName);
		Connection connection = MetaManager.connection(connectionInfo, database);
		StringBuffer sql = new StringBuffer("insert into ");
		sql.append(tableName);
		String columns = StringUtils.join(dataMap.keySet(),',');
		sql.append("(").append(columns).append(") values (");
		String values = StringUtils.join(dataMap.values(),"','");
		sql.append("'"+values+"'");
		sql.append(")");
		logger.debug("添加数据 sql:"+sql.toString());
		QueryRunner queryRunner = new QueryRunner();
		try {
			int update = queryRunner.update(connection, sql.toString());
			return "成功添加数据:"+update+" 条";
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DbUtils.closeQuietly(connection);
		}
		return "添加数据失败";
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-14下午2:24:53<br/>
	 * 功能:随机生成大量数据 <br/>
	 * @param connName 连接名称
	 * @param database 数据库名称
	 * @param tableName 表名称
	 * @param count 生成数量
	 * @param dataMap 固定字段和值
	 * @return
	 */
	public void writeMultiData(String connName,String database,String tableName,String count,Map<String,String> dataMap){
		Table table = MetaManager.table(connName, database, tableName);
		if(table == null){
			return ;
		}
		int countInt = 0;
		if(StringUtils.isNotBlank(count)){
			countInt = Integer.parseInt(count);
		}
		BigDataWriteThread bigDataWriteThread = new BigDataWriteThread(table, countInt);
		executorService.submit(bigDataWriteThread);
	}
	
	
	/********************sql 区*******************************/
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21上午11:43:19<br/>
	 * 功能:保存今天写过的 sql <br/>
	 * 入参: sql 语句 <br/>
	 * @throws IOException 
	 */
	public int saveSql(String fileName,String sqls) throws IOException{
		File filePath = null;
		if(StringUtils.isBlank(fileName)){
			String nowDay = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");
			filePath = new File(sqlBaseDir,nowDay+".sr");
		}else{
			filePath = new File(sqlBaseDir,fileName);
			if(!filePath.exists()){
				throw new IllegalArgumentException("文件路径不存在:"+filePath);
			}
		}
		FileUtils.writeStringToFile(filePath, sqls,"utf-8");
		return 0;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21上午11:44:47<br/>
	 * 功能: 读取保存过的 sql 列表<br/>
	 * 入参: <br/>
	 * @param currentPage 当前页 从 0 开始 
	 * @param pageSize 每页大小
	 * @return 
	 */
	public FileListResult sqlList(int currentPage,int pageSize){
		FileListResult fileListResult = new FileListResult();
		File[] listFiles = sqlBaseDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String name = pathname.getName();
				String extension = FilenameUtils.getExtension(name);
				return "sr".equals(extension);
			}
		});
		fileListResult.setTotal(listFiles.length);
		//按照日期排序文件
		Arrays.sort(listFiles,new Comparator<File>() {
			@Override
			public int compare(File file1, File file2) {
				long diff = file1.lastModified() - file2.lastModified();
				if(diff > 0){
					return 1;
				}
				return diff == 0 ? 0:-1;
			}
		});
		int fileCount = listFiles.length;
		int pageCount = (fileCount - 1)/pageSize + 1;
		if(currentPage < 0 || currentPage > pageCount){
			throw new IllegalArgumentException("页数超出范围");
		}
		//计算开始结束位置
		int startOffset = currentPage * pageSize;
		int endOffset = (currentPage + 1) * pageSize;
		if(endOffset > fileCount){endOffset = fileCount;}
		
		//获取到需要显示的文件列表
		List<FileInfo> showFileList = new ArrayList<FileInfo>();
		for(int i=startOffset;i<endOffset;i++){
			File currentFile = listFiles[i];
			FileInfo fileInfo = new FileInfo();
			fileInfo.setName(currentFile.getName());
			fileInfo.setLastModified(DateFormatUtils.format(currentFile.lastModified(), "yyyy-MM-dd HH:mm:ss"));
			fileInfo.setPath(currentFile.getParent());
			showFileList.add(fileInfo);
		}
		
		fileListResult.setFiles(showFileList);
		return fileListResult;
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-4-21下午2:27:08<br/>
	 * 功能:读取文件中的 sql 内容 <br/>
	 * 入参: <br/>
	 */
	public String readSqls(String fileName) throws IOException{
		File file = new File(sqlBaseDir+"/"+fileName);
		if(!file.exists()){
			return "文件不存在";
		}
		return FileUtils.readFileToString(file);
	}
	
	/*************************数据转移区*************************************/
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-22下午3:22:47<br/>
	 * 功能:数据转移,将当前选中的表的数据,转移到指定连接=>库=>表,
	 * 需先写好处理类放入 com.sanri.app.jdbc.datatransfer.impl 路径中,然后才能使用 <br/>
	 * @param conn 数据源连接 
	 * @param db 数据源库
	 * @param table 数据源表
	 * @param handlerClazz 处理类,需提供全路径
	 */
	public void transfer(String conn,String db,String table,String handlerClazz){
		if(StringUtils.isBlank(handlerClazz)){
			throw new IllegalArgumentException("请提供处理类");
		}
		//对于转移请求,需要排队,等上个请求处理完后再来下个请求
		synchronized (SqlClientServlet.class) {
			try {
				Class<?> handlerClazzImpl = Class.forName(handlerClazz);
				DataTransfer dataTransfer = (DataTransfer) handlerClazzImpl.newInstance();
				
				ConnectionInfo connectionInfo = MetaManager.getConnectionInfo(conn);
				Connection connection = MetaManager.connection(connectionInfo,db);
				
				//先查询需要转移的数据有多少
				QueryRunner queryRunner = new QueryRunner();
				Long recordCount = queryRunner.query(connection, "select count(1) from "+table, new ResultSetHandler<Long>(){
					@Override
					public Long handle(ResultSet resultset) throws SQLException {
						while(resultset.next()){
							return resultset.getLong(1);
						}
						return null;
					}
					
				});
				
				//开始数据转移
				long pertransfer = 1000;										//每次转移 1000 条数据
				int transferCount = (int) ((recordCount - 1)/pertransfer + 1);	//需要转移的次数
				int currentTransfer = 0; 
				logger.info("总共有 "+recordCount+" 条记录,计划每次转移 "+pertransfer+" 条数据,需转移 "+transferCount+" 次");
				Table tableEntity = MetaManager.table(conn,db, table);
				while(transferCount -- > 0){ 
					long startOffset = currentTransfer * pertransfer;
					long endOffset = (currentTransfer + 1) * pertransfer;
					if(endOffset > recordCount){endOffset = recordCount;}
					logger.info("转移第 "+(currentTransfer + 1) + " 批数据始末位置分别为 "+startOffset+":"+endOffset);
					
					List<Map<String, String>> queryData = queryRunner.query(connection, "select * from "+table+" limit "+startOffset+","+endOffset, new ResultSetHandler<List<Map<String,String>>>(){
						@Override
						public List<Map<String, String>> handle(ResultSet resultset) throws SQLException {
							ResultSetMetaData metaData = resultset.getMetaData();
							int columnCount = metaData.getColumnCount();
							List<Map<String, String>> listMap = new ArrayList<Map<String,String>>();
							while(resultset.next()){
								Map<String,String> map = new HashMap<String, String>();
								for(int i=1;i<=columnCount;i++){
									String columnName = metaData.getColumnName(i);
									String columnValue = resultset.getString(i);
									map.put(columnName, columnValue);
								}
								listMap.add(map);
							}
							return listMap;
						}
					});
					dataTransfer.handler(queryData,tableEntity);
					currentTransfer ++;
				}
				logger.info("数据转移成功");
				DbUtils.close(connection);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("处理类未找到");
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		}
	}
}
