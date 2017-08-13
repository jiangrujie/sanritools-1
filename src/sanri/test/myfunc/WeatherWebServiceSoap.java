package sanri.test.myfunc;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService
public interface WeatherWebServiceSoap {
	@WebMethod(action="http://WebXml.com.cn/getWeatherbyCityName")
	@WebResult(name = "getWeatherbyCityNameResult", targetNamespace = "http://WebXml.com.cn/")
	@RequestWrapper(localName = "getWeatherbyCityName", targetNamespace = "http://WebXml.com.cn/")
    @ResponseWrapper(localName = "getWeatherbyCityNameResponse", targetNamespace = "http://WebXml.com.cn/")
	ArrayOfString getWeatherbyCityName(
			@WebParam(name = "theCityName", targetNamespace = "http://WebXml.com.cn/")
			String theCityName);
}
