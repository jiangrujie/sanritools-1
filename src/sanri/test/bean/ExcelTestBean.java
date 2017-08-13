package sanri.test.bean;

import java.util.Date;

import sanri.utils.excel.Version;
import sanri.utils.excel.annotation.ExcelColumn;
import sanri.utils.excel.annotation.ExcelExport;
import sanri.utils.excel.annotation.ExcelImport;

@ExcelImport(startRow=1)
@ExcelExport(version=Version.EXCEL2007,autoWidth=true)
public class ExcelTestBean extends ExcelTestBeanSuper{
	@ExcelColumn(value="姓名",index=0,chineseWidth=true)
	private String name;
	@ExcelColumn(value="车架号",index=3)
	private String standno;
	@ExcelColumn(value="车辆价格",index = 2)
	private double price;
	@ExcelColumn(value="创建日期",index=1,pattern="yyyy-MM-dd")
	private Date createDate;
	public ExcelTestBean(){}
	public ExcelTestBean(String workorderNo, String name, String standno,double price,Date createDate) {
		super(workorderNo);
		this.name = name;
		this.standno = standno;
		this.price = price;
		this.createDate = createDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStandno() {
		return standno;
	}
	public void setStandno(String standno) {
		this.standno = standno;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
