package sanri.test.wspub;

import java.util.List;

import javax.jws.WebService;

import sanri.test.bean.SimpleStudent;

@WebService
public interface HelloService {
	List<String> getWeatherbyCityName(String theCityName);

	List<SimpleStudent> findAllStudent();
	
	void batchQuery(List<SimpleStudent> simpleStudents);
}
