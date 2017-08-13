package sanri.utils;

import java.io.File;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * 创建时间:2017-2-26上午10:27:01<br/>
 * 创建者:sanri<br/>
 * 功能:字符串工具类,弃用,直接使用 StringUtils 代替
 * <br/>
 */
@Deprecated
public class StringUtil extends StringUtils{
	/**
	 * 得到后缀
	 * 取最后一个点后面的内容,包含点
	 * @deprecated use @link{FilenameUtils.getExtension(filename)}
	 */
	public static String suffix(String path){
		String suffix = "";
		if(!isBlank(path)){
			int pos = path.lastIndexOf(".");
			if(pos != -1){
				suffix = path.substring(pos);
			}
		}
		return suffix;
	}
	/**
	 * 得到前缀
	 * 取最后一个点前面的内容,无点的情况返回空 FilenameUtils.getPath(filename)+FilenameUtils.getBaseName(filename)
	 * @deprecated 
	 */
	public static String prefix(String path){
		if(!isBlank(path)){
			int pos = path.lastIndexOf(".");
			if(pos != -1){
				return path.substring(0,pos);
			}
		}
		return path;
	}
	/**
	 * 
	 * 功能:取文件名<br/>
	 * 创建时间:2016-9-24下午4:37:52<br/>
	 * 作者：sanri<br/>
	 * 入参说明:路径<br/>
	 * 出参说明：文件名<br/>
	 * @deprecated use @link{FilenameUtils.getBaseName(filename)}
	 */
	public static String basename(String path){
		File file = new File(path);
		if(!file.exists()){
			return "";
		}
		if(!file.isFile()){
			return "";
		}
		return file.getName();
	}
	/**
	 * 功能:获取路径<br/>
	 * 创建时间:2016-9-24下午4:48:37<br/>
	 * 作者：sanri<br/>
	 * 入参说明:路径<br/>
	 * 出参说明：目录<br/>
	 * @param path
	 * @return<br/>
	 * @deprecated  use @link{ FilenameUtils.getPath(filename)}
	 */
	public static String dirname(String path){
		File file = new File(path);
		if(!file.exists()){
			return "";
		}
		if(file.isFile()){
			return file.getParent();
		}
		return file.getPath();
	}
	
	
}
