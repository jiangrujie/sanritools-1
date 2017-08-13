package sanri.test.bean;

import sanri.utils.excel.annotation.ExcelColumn;

public class ExcelTestBeanSuper {
	@ExcelColumn(index=4,value="工单号")
	private String workorderNo;
	public ExcelTestBeanSuper(){}
	public ExcelTestBeanSuper(String workorderNo) {
		super();
		this.workorderNo = workorderNo;
	}

	public String getWorkorderNo() {
		return workorderNo;
	}

	public void setWorkorderNo(String workorderNo) {
		this.workorderNo = workorderNo;
	}
}
