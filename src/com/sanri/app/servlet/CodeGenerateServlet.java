package com.sanri.app.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import sanri.utils.PathUtil;
import sanri.utils.Validate;
import sanri.utils.VelocityUtil;
import sanri.utils.ZipUtil;

import com.sanri.app.BaseServlet;
import com.sanri.app.jdbc.MetaManager;
import com.sanri.app.jdbc.Table;
import com.sanri.app.jdbc.codegenerate.GenerateConfig;
import com.sanri.app.jdbc.codegenerate.JavaPojo;
import com.sanri.app.jdbc.codegenerate.RenamePolicy;
import com.sanri.app.jdbc.codegenerate.RenamePolicyDefault;
import com.sanri.frame.RequestMapping;

@RequestMapping("/code")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CodeGenerateServlet extends BaseServlet {
	
	//类型映射配置 
	public final static Map<String,String> TYPE_MIRROR_MAP = new HashMap<String, String>();
	private static File pojoPath = null;
	private static File projectPath = null;
	private static File projectCodePath = null;
	
	static{
		//读取类型映射配置
		try {
			Properties properties = new Properties();
			String pkgPath = PathUtil.pkgPath("com.sanri.config");
			FileInputStream fileInputStream = new FileInputStream(new File(pkgPath+"/mapper_jdbc_java.properties"));
			properties.load(fileInputStream);
			TYPE_MIRROR_MAP.putAll((Map)properties);
			File generateDir = new File(dataTempPath,"generate");
			if(!generateDir.exists()){
				generateDir.mkdirs();
			}
			pojoPath = new File(generateDir,"pojo");
			projectPath = new File(generateDir,"project");
			projectCodePath  = new File(generateDir,"projectCode");
			if(!pojoPath.exists()){
				pojoPath.mkdir();
			}
			if(!projectPath.exists()){
				projectPath.mkdir();
			}
			if(!projectCodePath.exists()){
				projectCodePath.mkdir();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 功能: 构建 java pojo 对象<br/>
	 * 创建时间:2017-7-8下午5:20:46<br/>
	 * 作者：sanri<br/>
	 * @param connName 		连接名
	 * @param dbName		库名 
	 * @param tableName		表名
	 * @param model			生成模式
	 * @param packageName   包名
	 * @param baseEntity	基类
	 * @param interfaces	实现的接口
	 * @param excludeColums 排除的列
	 * @param 命名策略 ,因为暂时只有下划线转驼峰,所以暂时不管
	 * <br/>
	 */
	//使用默认命名策略
	private RenamePolicy renamePolicy = new RenamePolicyDefault(TYPE_MIRROR_MAP);
	@RequestMapping("/build/javabean")
	public String buildJavaBean(String connName,String dbName,String tableName,String model,String packageName,String baseEntity,String [] interfaces,String[] excludeColumns){
		Table table = MetaManager.table(connName, dbName, tableName);
		if(table == null){
			return "";
		}
		JavaPojo javapojo = new JavaPojo(model, packageName, table,renamePolicy);
		if(StringUtils.isNotBlank(baseEntity)){
			javapojo.setExtendsName(baseEntity);
		}
		if(!Validate.isEmpty(interfaces)){
			javapojo.setInterfaces(Arrays.asList(interfaces));
		}
		if(!Validate.isEmpty(excludeColumns)){
			javapojo.setExcludeColumns(Arrays.asList(excludeColumns));
		}
		List<String> javaCode = javapojo.build();
		File writerBean = javapojo.writerBean(javaCode,pojoPath);
		return writerBean.getName();
	}
	
	/**
	 * 
	 * 功能:下载文件<br/>
	 * 创建时间:2017-7-9上午8:20:32<br/>
	 * 作者：sanri<br/>
	 * @param typeName 类型名称 pojo/project/projectCode
	 * @param fileName 文件名称 
	 * @param request
	 * @param response<br/>
	 */
	public void downFile(String typeName,String fileName,HttpServletRequest request,HttpServletResponse response){
		File filePath = null;
		if("pojo".equals(typeName)){
			filePath = pojoPath;
		}else if("project".equals(typeName)){
			filePath = projectPath;
		}else if("projectCode".equals(typeName)){
			filePath = projectCodePath;
		}else{
			throw new IllegalArgumentException("不支持的类型");
		}
		File downFile = new File(filePath,fileName);
		if(!downFile.exists()){
			throw new IllegalArgumentException("文件不存在");
		}
		File targetFile = downFile;
		if(downFile.isDirectory()){
			targetFile = new File(downFile.getParent(),downFile.getName()+".zip");
			ZipUtil.zip(downFile, targetFile);
		}
		try {
			download(request, response, targetFile.getName(), new FileInputStream(targetFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 功能:加载模板<br/>
	 * 创建时间:2017-7-15上午10:09:43<br/>
	 * 作者：sanri<br/>
	 * @return<br/>
	 * @throws IOException 
	 */
	public Map<String,String> loadTemplate(String frameworkName,String templateName) throws IOException{
		String templatePath = PathUtil.pkgPath("com.sanri.config.templates");
		Map<String,String> ret = new HashMap<String, String>();
		File tplFile = new File(templatePath,templateName+".tpl");
		if(!tplFile.exists()){
			File templateDir = new File(templatePath,frameworkName);
			tplFile = new File(templateDir,templateName+".tpl");
			if(!tplFile.exists()){
				ret.put("result", "-1");
				return ret;
			}
		}
		String readFileToString = FileUtils.readFileToString(tplFile,"utf-8");
		ret.put("result", "0");
		ret.put("template", readFileToString);
		return ret;
	}
	
	/***
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-21下午4:43:36<br/>
	 * 功能:项目构建  <br/>
	 * @param generateConfig
	 * @return
	 */
	public String buildProject(GenerateConfig generateConfig) throws IllegalArgumentException{
		String model = generateConfig.getModel();
		String framework = generateConfig.getFramework();
		String connName = generateConfig.getConnName();
		String dbName = generateConfig.getDbName();
		if(StringUtils.isBlank(model) || StringUtils.isBlank(framework) || StringUtils.isBlank(connName) || StringUtils.isBlank(dbName)){
			throw new IllegalArgumentException("参数错误:model:"+model+",framework:"+framework+",connName:"+connName+",dbName:"+dbName);
		}
		List<String> tables = generateConfig.getTables();
		if(Validate.isEmpty(tables)){
			//没有需要生成的数据
			return "";
		}
		//获取元数据信息
		List<Table> tableMetas = MetaManager.tables(connName, dbName, tables);
		//生成的代码命名方式为 connName_dbName_时间戳
		String generateFileName = connName +"_"+dbName+System.currentTimeMillis();
		File generateFileDir = new File(projectCodePath,generateFileName);
		generateFileDir.mkdir();
		//开始生成
		if(!Validate.isEmpty(tableMetas)){
			if("ssm$ssh".indexOf(framework) == -1){
				throw new IllegalArgumentException("不支持的框架:"+framework);
			}
			//生成 controller,entity,service,serviceimpl,dao,daoimpl,xml 目录
			File controllerDir = buildPackageDir(generateFileDir,generateConfig.getControllerPackage());
			File entityDir = buildPackageDir(generateFileDir,generateConfig.getEntityPackage());
			File serviceDir = buildPackageDir(generateFileDir,generateConfig.getServicePackage());
			File serviceImplDir = buildPackageDir(generateFileDir,generateConfig.getServiceimplPackage());
			File daoDir = buildPackageDir(generateFileDir,generateConfig.getDaoPackage());
			File daoImplDir = buildPackageDir(generateFileDir,generateConfig.getDaoimplPackage());
			File xmlDir = new File(generateFileDir,"xml");
			
			for (Table table : tableMetas) {
				//对于每一个表,先生成 javaPojo 
				JavaPojo javaPojo = new JavaPojo(model, generateConfig.getEntityPackage(), table, renamePolicy);
				String excludeColumns = generateConfig.getExcludeColumns();
				if(StringUtils.isNotBlank(excludeColumns)){
					String[] excludeColumnArray = excludeColumns.split(",");
					javaPojo.setExcludeColumns(Arrays.asList(excludeColumnArray));
				}
				String interfaces = generateConfig.getInterfaces();
				if(StringUtils.isNotBlank(interfaces)){
					String[] interfaceArray = interfaces.split(",");
					javaPojo.setInterfaces(Arrays.asList(interfaceArray));
				}
				List<String> pojoJavaCode = javaPojo.build();
				javaPojo.writerBean(pojoJavaCode, entityDir);
				
				//获取上下文信息
				Map<String,Object> context = new HashMap<String, Object>();
				String className = javaPojo.getClassName();
				String lowEntityName = StringUtils.uncapitalize(className);
				context.put("lowEntity", lowEntityName);
				context.put("entity", className);
				context.put("basePackage", generateConfig.getBasePackage());
				context.put("controllerPackage", generateConfig.getControllerPackage());
				context.put("servicePackage", generateConfig.getServicePackage());
				context.put("serviceImplPackage", generateConfig.getServiceimplPackage());
				context.put("daoPackage", generateConfig.getDaoPackage());
				context.put("daoImplPackage", generateConfig.getDaoimplPackage());
				context.put("entityPackage", generateConfig.getEntityPackage());
				context.put("tableName", table.getTableName());
				context.put("chineseEntity",table.getComments());
				context.put("datetime", DateFormatUtils.format(System.currentTimeMillis(), datetimePattern));
				
				logger.info("正在以 "+generateConfig.getFramework()+" 框架生成表 :"+table.getTableName()+" 的文件代码,使用上下文:"+context);
				
				//先生成通用的 controller,service,serviceImpl
				Map<String, String> templates = generateConfig.getTemplates();
				String controllerCode = VelocityUtil.formatterString(templates.get("controller"), context);
				String serviceCode = VelocityUtil.formatterString(templates.get("service"), context);
				String serviceImplCode = VelocityUtil.formatterString(templates.get("serviceimpl"), context);
				
				try {
					FileUtils.writeStringToFile(new File(controllerDir,className+"Controller.java"), controllerCode);
					FileUtils.writeStringToFile(new File(serviceDir,className+"Service.java"), serviceCode);
					FileUtils.writeStringToFile(new File(serviceImplDir,className+"ServiceImpl.java"), serviceImplCode);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//其它文件用模板生成
				if("ssm".equalsIgnoreCase(framework)){
					ssmGenerate(generateConfig,context,javaPojo,daoDir,xmlDir);
				}else if("ssh".equalsIgnoreCase(framework)){
					sshGenerate(generateConfig,context,javaPojo,daoDir,daoImplDir);
				}
			}
		}
		
		String fileName = generateFileDir.getName();
		File zipProjectCodeFile = new File(generateFileDir.getParentFile(), fileName+".zip");
		ZipUtil.zip(generateFileDir,zipProjectCodeFile);
		return zipProjectCodeFile.getName();
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-21下午6:38:09<br/>
	 * 功能:建立包路径 <br/>
	 * @param baseDir
	 * @param package_
	 * @return
	 */
	private File buildPackageDir(File baseDir,String package_){
		if(StringUtils.isBlank(package_)){
			return baseDir;
		}
		String[] dirs = package_.split("\\.");
		if(!Validate.isEmpty(dirs)){
			File parentDir = baseDir;
			for (String dir : dirs) {
				parentDir = new File(parentDir,dir);
				if(!parentDir.exists()){
					parentDir.mkdir();
				}
			}
			return parentDir;
		}
		return baseDir;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-21下午6:02:25<br/>
	 * 功能:ssh 项目代码生成 <br/>
	 * @param generateConfig
	 * @param javaPojo
	 * @param daoImplDir 
	 * @param daoDir 
	 */
	private void sshGenerate(GenerateConfig generateConfig,Map<String,Object> context,JavaPojo javaPojo, File daoDir, File daoImplDir) {
		Map<String, String> templates = generateConfig.getTemplates();
		String daoCode = VelocityUtil.formatterString(templates.get("dao"), context);
		String daoImplCode = VelocityUtil.formatterString(templates.get("daoimpl"), context);
		
		try {
			String className = javaPojo.getClassName();
			FileUtils.writeStringToFile(new File(daoDir,className+"Dao.java"),daoCode );
			FileUtils.writeStringToFile(new File(daoImplDir,className+"DaoImpl.java"), daoImplCode);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-21下午6:02:37<br/>
	 * 功能:ssm 项目代码生成 <br/>
	 * @param generateConfig
	 * @param table
	 * @param javaPojo
	 * @param xmlDir 
	 * @param mapperDir 
	 * @return 
	 */
	private void ssmGenerate(GenerateConfig generateConfig,Map<String,Object> context,JavaPojo javaPojo,  File mapperDir, File xmlDir) {
		Map<String, String> templates = generateConfig.getTemplates();
		String daoCode = VelocityUtil.formatterString(templates.get("mapper"), context);
		String daoImplCode = VelocityUtil.formatterString(templates.get("xml"), context);
		
		try {
			String className = javaPojo.getClassName();
			FileUtils.writeStringToFile(new File(mapperDir,className+"Mapper.java"),daoCode );
			FileUtils.writeStringToFile(new File(xmlDir,className+".xml"), daoImplCode);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
