package sanri.test.wspub;

import java.util.List;

import sanri.test.bean.SimpleStudent;

public interface ABC {
	List<String> getWeatherbyCityName(String theCityName);

	List<SimpleStudent> findAllStudent();
}
