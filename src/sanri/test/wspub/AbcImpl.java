package sanri.test.wspub;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.alibaba.fastjson.JSONArray;

import sanri.test.bean.SimpleStudent;

@WebService
public class AbcImpl implements ABC {

	@Override
	@WebMethod
	public List<String> getWeatherbyCityName(@WebParam(name="theCityName")String theCityName) {
		List<String> weathers = new ArrayList<String>();
		weathers.add("6月19日 大到暴雨转大雨");
		weathers.add("59493");
		return weathers;
	}
	
	@WebMethod
	public List<SimpleStudent> findAllStudent(){
		List<SimpleStudent> simpleStudents = new ArrayList<>();
		SimpleStudent simpleStudent1 = new SimpleStudent("李仲","14111501123");
		SimpleStudent simpleStudent2 = new SimpleStudent("大黄","14211501123");
		SimpleStudent simpleStudent3 = new SimpleStudent("小明","14311501123");
		simpleStudents.add(simpleStudent1);
		simpleStudents.add(simpleStudent2);
		simpleStudents.add(simpleStudent3);
		return simpleStudents;
	}

}
