package sanri.utils.excel;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import sanri.utils.excel.annotation.ExcelColumn;
import sanri.utils.excel.annotation.ExcelExport;
import sanri.utils.excel.annotation.ExcelImport;
import sanri.utils.excel.exception.ConfigException;
import sanri.utils.excel.exception.ParseException;


/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-8-12下午1:43:19<br/>
 * 功能:导入导出工具类,
 * 这里导出只支持单个 sheet 页,如需要使用多 sheet 页导出,使用 ExcelWriter 类 <br/>
 */
public class ExcelUtil<T> {
	public final static float BASE_WIDTH_1_PX = 35.7f;
	public final static float BASE_HEIGHT_1_PX = 15.625f;
	public final static float BASE_CHINESE = 2 * 256;
	
	private Workbook workbook = null;
	private Version version;
	public ExcelUtil(Version version){
		if(version == Version.EXCEL2003){
			workbook = new HSSFWorkbook();
		}else if(version == Version.EXCEL2007){
			workbook = new XSSFWorkbook();
		}
		this.version = version;
	}

    /**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-12下午1:53:58<br/>
	 * 功能: 导出 excel <br/>
	 * @param title 标题
	 * @param data 数据列表
	 * @param titleStyle 标题样式
	 * @param headStyle 头部样式
	 * @param bodyStyle 主体数据样式
	 * @return
	 */
	public  Workbook export(String title,List<T> data,CellStyle titleStyle,CellStyle headStyle,CellStyle bodyStyle){
		if(data == null || data.size() == 0){
			//无数据直接反回空
			return null;
		}
		//取得当前需要导出的类型
		T dataType = data.get(0);
		Class<? extends Object> clazz = dataType.getClass();
		ExcelExport excelExport = clazz.getAnnotation(ExcelExport.class);
		if(excelExport == null){
			throw new ConfigException("配置错误,需要在目标类加注解 ExcelExport 才可导出");
		}
		int sheetMaxRow = -1;
		if(excelExport.sheetMaxRow() == -1 && version == Version.EXCEL2003){
			//设置配置为最大行数  
			sheetMaxRow = 60000;
		}
		try {
			List<ColumnConfig> columnConfigs = parseColumnConfig(clazz,true);
			//计算数据是否超量,是否需要创建多个 sheet 
			List<Sheet> sheets  = new ArrayList<Sheet>();
			if(sheetMaxRow == -1 || data.size() <= sheetMaxRow){
				//只会创建一个 sheet 
				String sheetName = title;
				if(StringUtils.isBlank(sheetName)){
					sheetName = "全部数据";
				}
				Sheet createSheet = workbook.createSheet(sheetName);
				sheets.add(createSheet);
			}else{
				int sheetCount = (data.size() -1 ) / sheetMaxRow  + 1;
				for (int i = 0; i < sheetCount; i++) {
					String sheetName = title+"_part"+i;
					if(StringUtils.isBlank(title)){
						sheetName = "部分数据_part"+i;
					}
					Sheet createSheet = workbook.createSheet(sheetName);
					sheets.add(createSheet);
				}
			}
			//正式添加数据
			if(sheets.size() == 1){		//添加全部数据到一张 sheet 页中,如果只有一张 sheet 页的话
				Sheet sheet = sheets.get(0);
				int startRow = createSheetTitle(title,titleStyle,excelExport,columnConfigs,sheet);
				insertDataToSheet(sheet,data,columnConfigs,startRow,headStyle,bodyStyle,excelExport);
			}else{
				for (int i=0;i<sheets.size();i++) {
					Sheet sheet = sheets.get(i);
					//如果有标题,添加标题
					int startRow = createSheetTitle(title, titleStyle, excelExport, columnConfigs, sheet);
					//复制截断的数据,到数据表 sheet 页
					int startDataIndex = i * sheetMaxRow;
					int endDataIndex = (i + 1) * sheetMaxRow;
					if(endDataIndex > data.size()){
						endDataIndex = data.size();
					}
					List<T> partData = new ArrayList<T>();
					for (int j = startDataIndex; j < endDataIndex; j++) {
						partData.add(data.get(j));
					}
					insertDataToSheet(sheet,partData,columnConfigs,startRow,headStyle,bodyStyle,excelExport);
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return workbook;
	}

	/**
	 * 
	 * 功能:下面都是各种需要的导出方法重载<br/>
	 * 创建时间:2017-8-13上午8:57:04<br/>
	 * 作者：sanri<br/>
	 */
	public Workbook export(List<T> data,CellStyle headStyle,CellStyle bodyStyle){
		return export("", data, null, headStyle, bodyStyle);
	}
	public Workbook export(List<T> data){
		return export(data, null, null);
	}
	public Workbook export(String title,List<T> data,CellStyle titleStyle){
		return export(title, data, titleStyle,null,null);
	}

	/**
	 * @作者: sanri
	 * @时间: 2017/8/12 21:32
	 * @功能: 创建空的工作薄
	 * @param version
	 * @param tips 提示文本,调用导出为空时可使用此方法返回一个带提示文本的工作薄
	 */
	public static Workbook createEmptyWorkbook(Version version,String tips){
		Workbook workbook = null;
		if(version == Version.EXCEL2003){
			workbook = new HSSFWorkbook();
		}else{
			workbook = new XSSFWorkbook();
		}
		if(StringUtils.isNotBlank(tips)){
			Sheet sheet = workbook.createSheet();
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue(tips);
		}
		return workbook;
	}
	
	/**
	 * 
	 * 功能:创建单元格样式<br/>
	 * 创建时间:2017-8-13上午7:46:59<br/>
	 * 作者：sanri<br/>
	 * @param workbook
	 * @param font 字体设置
	 * @param background 背景色
	 * @param center 是否居中
	 * @param wrapText 是否自动换行
	 * @return<br/>
	 */
	public CellStyle createCellStyle(Font font,IndexedColors background,boolean center,boolean wrapText){
		CellStyle createCellStyle = workbook.createCellStyle();
		if(font != null){
			createCellStyle.setFont(font);
		}
		createCellStyle.setFillForegroundColor(background.getIndex());
		createCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		createCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		createCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		createCellStyle.setBorderRight(CellStyle.BORDER_THIN);
		createCellStyle.setBorderTop(CellStyle.BORDER_THIN);
		if(center){//水平居中,垂直居中
			createCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			createCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		}
		createCellStyle.setWrapText(wrapText);
		return createCellStyle;
	}
	
	/**
	 * 
	 * 功能:创建字体 <br/>
	 * 创建时间:2017-8-13上午7:37:34<br/>
	 * 作者：sanri<br/>
	 * @param workbook
	 * @param family 字体种类
	 * @param size 字体大小
	 * @param b 是否加粗
	 * @return<br/>
	 */
	public Font createCellFont(String family,IndexedColors color,short size,boolean b){
		Font createFont = workbook.createFont();
		createFont.setCharSet(Font.DEFAULT_CHARSET);
		createFont.setColor(color.getIndex());
		createFont.setFontName(family);
		createFont.setFontHeight(size);
		if(b){
			createFont.setBoldweight((short)700);
		}
		return createFont;
	}


	/**
	 * @作者: sanri
	 * @时间: 2017/8/12 21:37
	 * @功能: 从输入流导入 excel 数据,只支持一个 sheet 页
	 * @param in
	 */
	public static <T> List<T> importData(InputStream in,Class<T> clazz){
		ExcelImport excelImport = clazz.getAnnotation(ExcelImport.class);
		if(excelImport == null){
			throw new ConfigException("需要在目标类加注解 ExcelImport 才可实现导入");
		}
		List<T> data = new ArrayList<T>();
        try{
			Workbook workbook = null;
			if (!in.markSupported()) {
				in = new PushbackInputStream(in, 8);
			}
			Version[] support = excelImport.support();
			if(support.length == 2){
				if (POIFSFileSystem.hasPOIFSHeader(in)) {
					workbook =  new HSSFWorkbook(in);
				}
				if (POIXMLDocument.hasOOXMLHeader(in)) {
					workbook =  new XSSFWorkbook(OPCPackage.open(in));
				}
			}else{
				if(support[0] == Version.EXCEL2003 && POIFSFileSystem.hasPOIFSHeader(in)){
					workbook =  new HSSFWorkbook(in);
				}else if(support[0] == Version.EXCEL2007 && POIXMLDocument.hasOOXMLHeader(in) ){
					workbook =  new XSSFWorkbook(OPCPackage.open(in));
				}
			}
			if(workbook == null){
				throw new ParseException("无法解析的 excel 流");
			}
			
			//真正解析 excel 流,只解析第一个 sheet 页
			List<ColumnConfig> columnConfigs = parseColumnConfig(clazz,false);
			int startRow = excelImport.startRow();
			CreationHelper creationHelper = workbook.getCreationHelper();
			
			Sheet sheet = workbook.getSheetAt(0);
			int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
			for(int i = startRow;i<physicalNumberOfRows;i++){
				Row row = sheet.getRow(i);
				T dataItem = clazz.newInstance();
				data.add(dataItem);
				for (ColumnConfig columnConfig : columnConfigs) {
					try{
						int index = columnConfig.getIndex();
						Method writeMethod = columnConfig.getWriteMethod();
						Cell cell = row.getCell(index);
						invokeData(cell.getCellType(), cell, writeMethod, columnConfig, dataItem, creationHelper);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (ConfigException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	/**
	 * 
	 * 功能:注入数据,这里可能会有很多问题,还有数据小数点问题<br/>
	 * 创建时间:2017-8-12下午10:52:05<br/>
	 * 作者：sanri<br/>
	 * @param cellType
	 * @param cell
	 * @param writeMethod
	 * @param columnConfig
	 * @param dataItem
	 * @param creationHelper
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException<br/>
	 */
	private static void invokeData(int cellType,Cell cell,Method writeMethod,ColumnConfig columnConfig,Object dataItem,CreationHelper creationHelper) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Class<?> dataType = columnConfig.getDataType();
		switch (cellType) {
		case Cell.CELL_TYPE_BOOLEAN:
			boolean booleanCellValue = cell.getBooleanCellValue();
			if(dataType == Boolean.class || dataType == boolean.class){
				writeMethod.invoke(dataItem, booleanCellValue);
			}else{
				writeMethod.invoke(dataItem, String.valueOf(booleanCellValue));
			}
			break;
		case Cell.CELL_TYPE_BLANK:
			//null 值无需写入
			break;
		case Cell.CELL_TYPE_FORMULA:
			FormulaEvaluator formulaEvaluator = creationHelper.createFormulaEvaluator();
			CellValue cellValue = formulaEvaluator.evaluate(cell);
			int newCellType = cellValue.getCellType();
			invokeData(newCellType, cell, writeMethod, columnConfig, dataItem, creationHelper);
			break;
		case Cell.CELL_TYPE_NUMERIC:
			
			break;
		case Cell.CELL_TYPE_STRING:
			String stringCellValue = cell.getStringCellValue();
			if(dataType.isPrimitive() || dataType == Integer.class || dataType == Short.class
					|| dataType == Long.class || dataType == Float.class
					|| dataType == Double.class || dataType == Character.class
					|| dataType == Boolean.class || dataType == Byte.class ){
				writeMethod.invoke(dataItem, Double.parseDouble(stringCellValue));//TODO 每种都需要转换
			}else if(dataType == Date.class){
				try {
					writeMethod.invoke(dataItem, DateUtils.parseDate(stringCellValue, new String[]{columnConfig.getPattern()}));
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}else{
				writeMethod.invoke(dataItem, stringCellValue);
			}
			break;
		case Cell.CELL_TYPE_ERROR:
			//作为 null 值写入
			break;
		default:
			break;
		}
	}
	
	/**
	 * 功能:获取类的列配置<br/>
	 * 创建时间:2017-8-12下午10:08:21<br/>
	 * 作者：sanri<br/>
	 * @param clazz
	 * @param readWrite 解析读时为真,解析写时为假
	 * @return
	 * @throws IntrospectionException
	 * @throws ConfigException
	 * @throws NoSuchFieldException
	 * @throws SecurityException<br/>
	 */
	private static List<ColumnConfig> parseColumnConfig(Class<? extends Object> clazz,boolean readWrite) throws IntrospectionException,
			ConfigException, NoSuchFieldException, SecurityException {
		//获取列配置,所有需要导出的类,最后应该都是从 Object 继承
		BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		if(propertyDescriptors == null || propertyDescriptors.length == 0 ){
			//必须要有属性配置
			throw new ConfigException("bean 和其父类, 必须至少包含一个属性");
		}
		//获取 bean 上所有的列配置
		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			Method readMethod = propertyDescriptor.getReadMethod();
			Method writeMethod = propertyDescriptor.getWriteMethod();
			String propertyName = propertyDescriptor.getName();
			Class<?> propertyType = propertyDescriptor.getPropertyType();
			if(!typeSupport(propertyType)){
				throw new ConfigException("不支持的类型:"+propertyType);
			}
			//只导出属性可读的属性,没有 get 方法的属性不进行导出
			if((readMethod != null && readWrite) || (writeMethod != null && !readWrite)){
				//先从属性列上获取配置,如果属性列上没有,就从读方法上获取,并覆盖属性列上的配置
				ColumnConfig columnConfig = new ColumnConfig(propertyName, readMethod, writeMethod);
				columnConfigs.add(columnConfig);
				columnConfig.setDataType(propertyType);
				Field propertyField = null;
				Class<?> currentClass = clazz;
				while(currentClass != Object.class && propertyField == null){
					try{
						propertyField = currentClass.getDeclaredField(propertyName);
					}catch(NoSuchFieldException e){
						currentClass = clazz.getSuperclass();
					}
				}
				if(propertyField == null){
					throw new NoSuchFieldException("没有此属性:"+propertyName);
				}
				ExcelColumn excelColumn = propertyField.getAnnotation(ExcelColumn.class);
				if(excelColumn != null){
					columnConfig.config(excelColumn.value(), excelColumn.width(), excelColumn.index(), excelColumn.hidden(), excelColumn.pattern(),excelColumn.chineseWidth());
				}
				//使用方法上的配置,覆盖属性上的配置
				ExcelColumn methodExcelColumn = null;
				if(readWrite){
					//从读方法上覆盖配置
					methodExcelColumn = readMethod.getAnnotation(ExcelColumn.class);
				}else{
					//从写方法上覆盖配置
					methodExcelColumn = writeMethod.getAnnotation(ExcelColumn.class);
				}
				if(methodExcelColumn != null){
					columnConfig.config(methodExcelColumn.value(), methodExcelColumn.width(), methodExcelColumn.index(), methodExcelColumn.hidden(), methodExcelColumn.pattern(),excelColumn.chineseWidth());
				}
			}
		}
		//对导出的属性配置进行排序
		Collections.sort(columnConfigs);
		return columnConfigs;
	}

	/**
	 * 作者: sanri
	 * 时间 : 2017/08/12
	 * 功能 : 创建 sheet 标题,如果存在的话
	 * @param title
	 * @param titleStyle
	 * @param excelExport
	 * @param columnConfigs
	 * @param sheet
	 * @return 返回当前 sheet 起始行
	 */
	private static int createSheetTitle(String title, CellStyle titleStyle, ExcelExport excelExport, List<ColumnConfig> columnConfigs, Sheet sheet) {
		int startRow = 0;
		if(StringUtils.isNotBlank(title)){
	        Row titleRow = sheet.createRow(startRow++);
	        Cell titleCell = titleRow.createCell(0);
	        titleCell.setCellValue(title);
	        if(titleStyle != null){
	            titleCell.setCellStyle(titleStyle);
	        }
	        //合并单元格
	        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnConfigs.size() - 1));
	        short titleRowHeight = excelExport.titleRowHeight();
	        titleRowHeight = (short) (titleRowHeight * BASE_HEIGHT_1_PX);
	        titleRow.setHeight(titleRowHeight);
	    }
		return startRow;
	}

	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-12下午6:35:29<br/>
	 * 功能:添加数据到 sheet 页 <br/>
	 * @param <T>
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private static <T> void insertDataToSheet(Sheet sheet,List<T> partData, List<ColumnConfig> columnConfigs,int startRow,CellStyle headStyle,CellStyle bodyStyle,ExcelExport excelExport) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Row headRow = sheet.createRow(startRow++);
		headRow.setHeight((short)(excelExport.headRowHeight() * BASE_HEIGHT_1_PX));
		//创建标题列
		for (int i=0;i<columnConfigs.size();i++) {
			ColumnConfig columnConfig = columnConfigs.get(i);
			String chinese = columnConfig.getChinese();
			Cell headCell = headRow.createCell(i);
			headCell.setCellValue(chinese);
			if(headStyle != null){
				headCell.setCellStyle(headStyle);
			}
		}
		//创建数据列
		for (int i = 0; i < partData.size(); i++) {
			Row bodyRow = sheet.createRow(startRow ++);
			bodyRow.setHeight((short)(excelExport.bodyRowHeight() * BASE_HEIGHT_1_PX));
			T dataItem = partData.get(i);
			for (int j=0;j<columnConfigs.size();j++) {
				ColumnConfig columnConfig = columnConfigs.get(j);
				Method readMethod = columnConfig.getReadMethod();
				Cell bodyCell = bodyRow.createCell(j);
				if(bodyStyle != null){
					bodyCell.setCellStyle(bodyStyle);
				}
				Object cellData = readMethod.invoke(dataItem);
				Class<?> dataType = columnConfig.getDataType();
				if(dataType == Date.class){
					//获取日期对象数据
					Date cellDataReal = null;
					if(cellData != null){
						cellDataReal = (Date)cellData;
					}

					//如果是日期类型,则调用转换规则进行转换
					String pattern = columnConfig.getPattern();
					if(StringUtils.isBlank(pattern)){
						//如果是空格式,直接设置日期数据
						bodyCell.setCellValue(cellDataReal);
					}else{
						if(cellDataReal != null){
							bodyCell.setCellValue(DateFormatUtils.format(cellDataReal,pattern));
						}
					}
				}else if(dataType == Boolean.class || dataType == boolean.class){
					//必须有值
					boolean cellBooleanReal = Boolean.parseBoolean(ObjectUtils.toString(cellData));
					if(cellBooleanReal){
						bodyCell.setCellValue("是");
					}else{
						bodyCell.setCellValue("否");
					}
				}else{
					bodyCell.setCellValue(ObjectUtils.toString(cellData));
				}
			}
		}
		//设置列宽
		boolean autoWidth = excelExport.autoWidth();
		if(autoWidth){
			for (int i=0;i<columnConfigs.size();i++) {
				sheet.autoSizeColumn(i);
				ColumnConfig columnConfig = columnConfigs.get(i);
				if(columnConfig.isChineseWidth()){
					int width = sheet.getColumnWidth(i);
					sheet.setColumnWidth(i, width * 2);
				}
			}
		}else{
			for (int i = 0; i < columnConfigs.size(); i++) {
				ColumnConfig columnConfig = columnConfigs.get(i);
				int width = columnConfig.getWidth();
				if(width < columnConfig.getChinese().length()){
					//如果默认宽度是小于了中文字的宽度,则取中文字的宽度
					width = (int) (columnConfig.getChinese().length() * BASE_CHINESE);
				}
				sheet.setColumnWidth(i,width);
			}
		}
		//隐藏列配置
		for (int i = 0; i < columnConfigs.size(); i++) {
			ColumnConfig columnConfig = columnConfigs.get(i);
			boolean hidden = columnConfig.isHidden();
			sheet.setColumnHidden(i,hidden);
		}
	}
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-12下午2:29:59<br/>
	 * 功能: 是否支持给定的类型<br/>
	 * @param clazz
	 * @return
	 */
	private static boolean typeSupport(Class<?> clazz){
		return clazz.isPrimitive() || clazz == String.class
				|| clazz == Integer.class || clazz == Short.class
				|| clazz == Long.class || clazz == Float.class
				|| clazz == Double.class || clazz == Character.class
				|| clazz == Boolean.class || clazz == Byte.class
				|| clazz == Date.class;
	}
}
