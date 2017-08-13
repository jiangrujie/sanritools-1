package sanri.test.bean;

/**
 * 
 * 创建时间:2017-6-18上午10:25:36<br/>
 * 创建者:sanri<br/>
 * 功能: 人民<br/>
 */
public class People {
	//姓名
	protected String name;
	//身份证号
	protected String idCard;
	//性别 1:男,2:女,3:保秘
	protected int gender;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
}
