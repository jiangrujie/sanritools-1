package sanri.test.mini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-6-29下午3:41:26<br/>
 * 功能:合并代码 <br/>
 */
public class HbCode {
	
	public static void findFiles(File dir,List<File> files){
		if(dir.isDirectory()){
			File[] listFiles = dir.listFiles();
			for (File file : listFiles) {
				findFiles(file,files);
			}
		}else {
			files.add(dir);
		}
	}
	
	public static void main(String[] args) {
		List<File> files = new ArrayList<File>();
		File dir = new File("E:/project/xxx/hhx-gpx-web-n/src/main/java");
		File destFile = new File("d:/hb.java");
		findFiles(dir, files);
		for (File file : files) {
			try {
				String readFileToString = FileUtils.readFileToString(file);
				FileUtils.writeStringToFile(destFile, readFileToString,true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
