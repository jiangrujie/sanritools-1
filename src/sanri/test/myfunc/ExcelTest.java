package sanri.test.myfunc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import sanri.test.bean.ExcelTestBean;
import sanri.utils.RandomUtil;
import sanri.utils.excel.ExcelUtil;
import sanri.utils.excel.Version;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-8-12下午12:04:35<br/>
 * 功能:测试 excel 工具类 <br/>
 */
public class ExcelTest {

    @Test
    public void testExport() throws IOException{
    	List<ExcelTestBean> data = new ArrayList<ExcelTestBean>();
    	Date nowDate = new Date();
    	for (int i = 0; i < 15; i++) {
    		ExcelTestBean excelTestBean = new ExcelTestBean(RandomUtil.randomNumeric(3), RandomUtil.username(), RandomUtil.randomAlphanumeric(15), Double.parseDouble(RandomUtil.randomNumeric(5)),nowDate);
    		data.add(excelTestBean);
		}
    	ExcelUtil<ExcelTestBean> excelUtil = new ExcelUtil<>(Version.EXCEL2007);
    	Workbook workbook = excelUtil.export(data);
    	FileOutputStream fos = new FileOutputStream("d:/test/testbean.xlsx");
    	if(workbook == null){
    		workbook = ExcelUtil.createEmptyWorkbook(Version.EXCEL2007, "无数据");
    	}
    	workbook.write(fos);
    	fos.flush();
    	fos.close();
    }
    
    @Test
    public void testExportTitle() throws IOException{
    	List<ExcelTestBean> data = new ArrayList<ExcelTestBean>();
    	Date nowDate = new Date();
    	for (int i = 0; i < 15; i++) {
    		ExcelTestBean excelTestBean = new ExcelTestBean(RandomUtil.randomNumeric(3), RandomUtil.username(), RandomUtil.randomAlphanumeric(15), Double.parseDouble(RandomUtil.randomNumeric(5)),nowDate);
    		data.add(excelTestBean);
		}
    	ExcelUtil<ExcelTestBean> excelUtil = new ExcelUtil<>(Version.EXCEL2007);
    	CellStyle createCellStyle = excelUtil.createCellStyle(null, IndexedColors.GREEN, true, false);
    	Workbook workbook = excelUtil.export("导出的标题",data,createCellStyle);
    	FileOutputStream fos = new FileOutputStream("d:/test/testbean.xlsx");
    	if(workbook == null){
    		workbook = ExcelUtil.createEmptyWorkbook(Version.EXCEL2007, "无数据");
    	}
    	workbook.write(fos);
    	fos.flush();
    	fos.close();
    }
    
    @Test
    public void testExportStyle1() throws IOException{
    	List<ExcelTestBean> data = new ArrayList<ExcelTestBean>();
    	Date nowDate = new Date();
    	for (int i = 0; i < 15; i++) {
    		ExcelTestBean excelTestBean = new ExcelTestBean(RandomUtil.randomNumeric(3), RandomUtil.username(), RandomUtil.randomAlphanumeric(15), Double.parseDouble(RandomUtil.randomNumeric(5)),nowDate);
    		data.add(excelTestBean);
		}
    	ExcelUtil<ExcelTestBean> excelUtil = new ExcelUtil<>(Version.EXCEL2007);
    	CellStyle headStyle = excelUtil.createCellStyle(null, IndexedColors.BLUE, true, false);
    	CellStyle bodyStyle = excelUtil.createCellStyle(null, IndexedColors.YELLOW, true, true);
    	Workbook workbook = excelUtil.export(data,headStyle,bodyStyle);
    	FileOutputStream fos = new FileOutputStream("d:/test/testbean.xlsx");
    	if(workbook == null){
    		workbook = ExcelUtil.createEmptyWorkbook(Version.EXCEL2007, "无数据");
    	}
    	workbook.write(fos);
    	fos.flush();
    	fos.close();
    }

    @Test
    public void testImport() throws FileNotFoundException {
        InputStream in = new FileInputStream("d:/test/testbean.xlsx");
        List<ExcelTestBean> importData = ExcelUtil.importData(in, ExcelTestBean.class);
        System.out.println(importData);
        
    }
}
