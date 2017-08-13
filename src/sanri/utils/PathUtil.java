package sanri.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
/**
 * 
 * 创建时间:2016-9-24上午7:30:24<br/>
 * 创建者:sanri<br/>
 * 功能:路径工具处理类<br/>
 * ROOT 路径是指 WebRoot 路径,也即 web 服务根路径
 */
public class PathUtil {
	
	public static URL URL = PathUtil.class.getResource("/");
	public static URI ROOT;
	static{
		try {
			ROOT = URL.toURI().resolve("../../");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 相对于根的路径
	 * @param relativePath
	 * @return
	 */
	public static String getPath(String relativePath){
		if(StringUtils.isBlank(relativePath)){
			return ROOT.getPath();
		}
		if(relativePath.startsWith("/")){
			relativePath = relativePath.substring(1);
		}
		
		return ROOT.resolve(relativePath).getPath();
	}
	
	/**
	 * 
	 * 功能: webapps 路径,项目上层路径<br/>
	 * 非 tomcat 容器将找不到 webapps 路径<br/>
	 * 创建时间:2016-9-24上午7:33:29<br/>
	 * 作者：sanri<br/>
	 */
	public static String webAppsPath(){
		return getPath("../");
	}
	/**
	 * 
	 * 功能:项目的 WEB-INF 路径,必须使用名字 WEB-INF 的路径才能取到<br/>
	 * 创建时间:2016-9-24上午7:34:38<br/>
	 * 作者：sanri<br/>
	 */
	public static String webinfPath(){
		return getPath("/WEB-INF");
	}
	
	public static String webPath(){
		return ROOT.getPath();
	}
	/**
	 * 
	 * 功能:类路径<br/>
	 * 创建时间:2016-9-24上午7:35:15<br/>
	 * 作者：sanri<br/>
	 */
	public static String classPath(){
//		return getPath("/WEB-INF/classes");
		return URL.getPath();
	}
	
	/**
	 * 
	 * 功能:包路径<br/>
	 * 创建时间:2016-9-24上午7:35:55<br/>
	 * 作者：sanri<br/>
	 */
	public static String pkgPath(String package_){
		String relative = package_.replaceAll("\\.", "/");
//		return getPath("/WEB-INF/classes/"+relative);
		URI resolve = null;
		try {
			resolve = URL.toURI().resolve(relative);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return resolve.getPath();
	}
	
//	public static void main(String[] args) {
//		String classPath = PathUtil.classPath();
//		String pkgPath = PathUtil.pkgPath("sanri.utils");
//		String pkgPathRoot = PathUtil.pkgPath("");
//		String webAppsPath = PathUtil.webAppsPath();
//		String webinfPath = PathUtil.webinfPath();
//		
//		Map<String,String> hashMap = new HashMap<String, String>();
//		hashMap.put("classPath", classPath);
//		hashMap.put("pkgPath", pkgPath);
//		hashMap.put("webAppsPath", webAppsPath);
//		hashMap.put("webinfPath", webinfPath);
//		hashMap.put("pkgPathRoot", pkgPathRoot);
//		
//		String jsonString = JSONObject.toJSONString(hashMap);
//		System.out.println(jsonString);
//	}
}
