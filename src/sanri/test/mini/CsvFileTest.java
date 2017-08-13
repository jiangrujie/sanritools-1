package sanri.test.mini;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CsvFileTest {
	private final static Log logger = LogFactory.getLog(CsvFileTest.class);
	public static void main(String[] args) {
		File dir = new File("d:/test");
		BufferedWriter csvFileWriter = null;
		try {
			File createTempFile = File.createTempFile("csvFile", ".csv",dir );
			logger.info("file :"+createTempFile);
			csvFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(createTempFile), "gbk"), 1024);
			csvFileWriter.write("name");
			csvFileWriter.write(",");
			csvFileWriter.write("sanri abc");
			csvFileWriter.newLine();
			csvFileWriter.write("sex");
			csvFileWriter.write(",");
			csvFileWriter.write("男");
			csvFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
