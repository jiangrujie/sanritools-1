package sanri.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-7-21下午6:47:59<br/>
 * 功能:模板生成工具 <br/>
 */
public class VelocityUtil {
	private static VelocityEngine velocityEngine = new VelocityEngine();
	static{
		//以后这个可以配置为从配置文件中读取
		Properties velocitySetting = new Properties();
		velocitySetting.setProperty(Velocity.ENCODING_DEFAULT, "utf-8");
		velocitySetting.setProperty(Velocity.INPUT_ENCODING, "utf-8");
		velocitySetting.setProperty(Velocity.OUTPUT_ENCODING, "utf-8");
		//设置这个在服务器运行不可靠,如在正式环境需改为 注释部分
//		velocitySetting.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocitySetting.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, PathUtil.ROOT.getPath());
		velocityEngine.init(velocitySetting);
	}
	/**
	 * 
	 * 功能:将字符串格式化<br/>
	 * 创建时间:2016-10-8上午11:16:38<br/>
	 * 作者：sanri<br/>
	 * @param source 源模板字符串
	 * @param data	数据
	 * @return 返回最终字符串<br/>
	 */
	public static String formatterString(String source,Map<String,Object> data){
		if(StringUtils.isBlank(source)){
			return source;
		}
		VelocityContext context = new VelocityContext();
		if(data != null && data.size() > 0){
			Iterator<Entry<String, Object>> dataIterator = data.entrySet().iterator();
			while(dataIterator.hasNext()){
				Entry<String, Object> dataEntry = dataIterator.next();
				String key = dataEntry.getKey();
				Object value = dataEntry.getValue();
				
				context.put(key, value);
			}
		}
		// 输出流  
		StringWriter writer = new StringWriter();  
		velocityEngine.evaluate(context, writer, "StringFormat", source);
		return writer.toString();
	}
	
	/**
	 * 
	 * 功能:从模板文件由数据生成到写流<br/>
	 * 创建时间:2016-9-25下午2:48:13<br/>
	 * 作者：sanri<br/>
	 */
	public static void generate(String tmplPath,Map<String,Object> data,Writer writer) throws IOException{
		VelocityContext context = new VelocityContext();
		if(data != null && data.size() > 0){
			Iterator<Entry<String, Object>> dataIterator = data.entrySet().iterator();
			while(dataIterator.hasNext()){
				Entry<String, Object> dataEntry = dataIterator.next();
				String key = dataEntry.getKey();
				Object value = dataEntry.getValue();
				
				context.put(key, value);
			}
		}
		Template template = velocityEngine.getTemplate(tmplPath);
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-28下午4:59:39<br/>
	 * 功能:读取文件中的模板进行数据替换 <br/>
	 * @param sourceFile
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static String formatterFile(File sourceFile,Map<String,Object> data) throws IOException{
		if(sourceFile == null || !sourceFile.exists()){
			return "";
		}
		String readFileToString = FileUtils.readFileToString(sourceFile, "utf-8");
		return formatterString(readFileToString, data);
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-7-28下午5:02:35<br/>
	 * 功能:从模板路径加载模板数据 <br/>
	 * @param tmplPath
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static String formatterFile(String tmplPath,Map<String,Object> data) throws IOException{
		StringWriter stringWriter = new StringWriter();
		generate(tmplPath, data, stringWriter);
		return stringWriter.toString();
	}
	
	public static void generate(String tmplPath,Map<String,Object> data,OutputStream os) throws IOException{
		OutputStreamWriter writer = new OutputStreamWriter(os,"utf-8");
		generate(tmplPath, data, writer);
	}
	
	public static void generateFile(String tmplPath,Map<String,Object> data,File file) throws IOException{
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		generate(tmplPath, data, fileOutputStream);
	}
	public static void generateFile(String tmplPath,Map<String,Object> data,String filePath) throws IOException{
		File file = new File(filePath);
		generateFile(tmplPath, data, file);
	}
}
