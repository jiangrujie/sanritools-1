//package sanri.test.myfunc;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Map;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.ParseException;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.HttpParams;
//import org.apache.http.util.EntityUtils;
//import org.junit.Test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.sanri.app.wscall.WsdlOperation;
//import com.sanri.app.wscall.WsdlPort;
//import com.sanri.app.wscall.WsdlService;
//import com.sanri.app.wscall.WsdlServiceLoader;
//
//public class TestWSCall {
//	
//	@Test
//	public void testNewParser(){
//		String wsdlURL = "http://localhost:8089/ws?wsdl";
////		String wsdlURL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
////		String wsdlURL = "http://ws.webxml.com.cn/WebServices/TrainTimeWebService.asmx?wsdl";
////		String wsdlURL = "http://www.webxml.com.cn/WebServices/MobileCodeWS.asmx?wsdl";
//		WsdlService loadService = WsdlServiceLoader.loadService(wsdlURL);
//		WsdlPort wsdlPort = loadService.getWsdlPort("AbcImplPort");	//AbcImplPort  WeatherWebServiceSoap
//		WsdlOperation wsdlOperation = wsdlPort.getWsdlOperation("getWeatherbyCityName"); //getWeatherbyCityName findAllStudent
////		String buildRequestTemplate = wsdlOperation.buildRequestTemplate();
////		System.out.println(buildRequestTemplate);
//		JSONObject jsonObject =  new JSONObject();
//		jsonObject.put("theCityName", "深圳");
//		JSONObject invoke = wsdlOperation.invoke(jsonObject);
//		System.out.println(invoke.toJSONString());
//	}
//	
//	@Test
//	public void testNewParser2(){
////		String wsdlURL = "http://www.webxml.com.cn/WebServices/IpAddressSearchWebService.asmx?wsdl";
//		String wsdlURL = "http://ws.webxml.com.cn/WebServices/TrainTimeWebService.asmx?wsdl";
////		String wsdlURL = "http://www.webxml.com.cn/WebServices/MobileCodeWS.asmx?wsdl";
//		WsdlService loadService = WsdlServiceLoader.loadService(wsdlURL);
//		Map<String, WsdlPort> wsdlPortMap = loadService.getWsdlPortMap();
//		System.out.println(wsdlPortMap);
//		WsdlPort wsdlPort = loadService.getWsdlPort("TrainTimeWebServiceSoap");
//		WsdlOperation wsdlOperation = wsdlPort.getWsdlOperation("getStationAndTimeByTrainCode");
////		String buildRequestTemplate = wsdlOperation.buildRequestTemplate();
////		System.out.println(buildRequestTemplate);
//		JSONObject jsonObject =  new JSONObject();
//		jsonObject.put("TrainCode", "G6034");
//		jsonObject.put("UserID", " ");
//		JSONObject result = wsdlOperation.invoke(jsonObject);
//		System.out.println(result.toJSONString());
//		
//	}
//	
//	/**
//	 * 
//	 * 作者:sanri <br/>
//	 * 时间:2017-6-22下午9:08:01<br/>
//	 * 功能:测试从文件系统加载 <br/>
//	 */
//	@Test
//	public void testLoadFromFile(){
//		File wsdlFile = new File("C:/Users/2/Desktop/weather.wsdl");
//		WsdlService loadService = WsdlServiceLoader.loadService(wsdlFile);
//		WsdlPort wsdlPort = loadService.getWsdlPort("WeatherWebServiceSoap");	//AbcImplPort  WeatherWebServiceSoap
//		WsdlOperation wsdlOperation = wsdlPort.getWsdlOperation("getWeatherbyCityName"); //getWeatherbyCityName findAllStudent
////		String buildRequestTemplate = wsdlOperation.buildRequestTemplate();
////		System.out.println(buildRequestTemplate);
//		JSONObject jsonObject =  new JSONObject();
//		jsonObject.put("theCityName", "深圳");
//		JSONObject invoke = wsdlOperation.invoke(jsonObject);
//		System.out.println(invoke.toJSONString());
//	}
//	
////	@Test
////	public void testLoadMethod(){
//////		String wsdlURL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
//////		String wsdlURL = "http://ws.webxml.com.cn/WebServices/TrainTimeWebService.asmx?wsdl";
////		String wsdlURL = "http://www.webxml.com.cn/WebServices/MobileCodeWS.asmx?wsdl";
////		WebServiceClass forName = WebServiceClass.forName(wsdlURL);
////		WebServiceMethod declaredMethod = forName.getDeclaredMethod("getWeatherbyCityName");
////		JSONObject jsonObject = new JSONObject();
////		jsonObject.put("theCityName", "西安");
////		Object invoke = declaredMethod.invoke(jsonObject);
////		System.out.println(invoke);
//////		WebServiceType returnType = declaredMethod.getReturnType();
//////		String cast = returnType.cast(invoke, String.class);
//////		System.out.println(cast);
////		
////	}
//	
////	@Test
////	public void testWsdl4j(){
////		WSDLFactory wsdlFactory = WSDLUtil.getWSDLFactory();
////		WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
////		wsdlReader.setFeature("javax.wsdl.verbose", true);
////		wsdlReader.setFeature("javax.wsdl.importDocuments", true);
////		try {
////			String wsdlURL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
////			Definition definition = wsdlReader.readWSDL(wsdlURL);
////			//获取命名空间
////			String targetNamespace = definition.getTargetNamespace();
////			System.out.println(targetNamespace);
////			
////		} catch (WSDLException e) {
////			e.printStackTrace();
////		}
////	}
//	
//	/**
//	 * 
//	 * 作者:sanri <br/>
//	 * 时间:2017-6-14下午4:35:33<br/>
//	 * 功能:测试类型解析 <br/>
//	 */
////	@Test
////	public void testTypesParser(){
////		WSDLFactory wsdlFactory = WSDLUtil.getWSDLFactory();
////		WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
////		wsdlReader.setFeature("javax.wsdl.verbose", true);
////		wsdlReader.setFeature("javax.wsdl.importDocuments", true);
////		try {
////			String wsdlURL = "http://www.webxml.com.cn/WebServices/MobileCodeWS.asmx?wsdl";
//////			String wsdlURL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
////			Definition definition = wsdlReader.readWSDL(wsdlURL);
////			WebServiceClassLoader.processTypes(definition);
////		} catch (WSDLException e) {
////			e.printStackTrace();
////		}
////	}
//	
////	public void processTypes(Definition defintion) {
////	    XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
////	    Types types = defintion.getTypes();//得到definition下一级中的types
////	    List list = types.getExtensibilityElements();
////	    for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
////	        SchemaImpl schemaImpl = (SchemaImpl) iterator.next();
////	        Element element = (Element) schemaImpl.getElement();
////	        XmlSchema xmlSchema = xmlSchemaCollection.read(element);
////
////	        XmlSchemaObjectTable smlSchemaObjectTable = xmlSchema.getSchemaTypes();
////	        //如图5，得到types下的complexType
////	        XmlSchemaObjectTable elements = xmlSchema.getElements();
////	        //如图5，得到types下的element
////
////	        Iterator elementsItr=elements.getValues();//对element进行进一步深入
////	        while(elementsItr.hasNext()){
////	            XmlSchemaElement elemt = (XmlSchemaElement) elementsItr.next();
////	            String elemtName = elemt.getName();
////	            if(elemtName.equals(CodeGenerate.getBox_function())){
////	                XmlSchemaType elemtNameType=elemt.getSchemaType();
////	                search (elemtNameType); //search函数用于对复杂类型进行专门的处理
////	            }
////	        }
////	        Iterator typesItr = smlSchemaObjectTable.getValues();//对complexType进行进一步深入
////	        while (typesItr.hasNext()) {
////	            XmlSchemaType type = (XmlSchemaType) typesItr.next();
////	            String typeName = type.getName();
////	            if(typeName.equals(complex_type)){
////	                search(type);
////	            }
////	        }
////	    }
////	}
////	
////	public void search(XmlSchemaType type) {
////	    //如果是复杂类型，则按照XmlSchemaComplexType-> XmlSchemaSequence-> XmlSchemaElement的层次进行解析，因为XmlSchemaElement可能任然是复杂类型，所以需要进一步判断递归调用Search。如图6，图7
////	    if (type instanceof XmlSchemaComplexType) {
////	        XmlSchemaComplexType xmlSchemaComplexType = (XmlSchemaComplexType) type;
////	        XmlSchemaParticle xmlSchemaParticle = xmlSchemaComplexType.getParticle();
////	        if (xmlSchemaParticle instanceof XmlSchemaSequence) {
////	            XmlSchemaSequence xmlSchemaSequence = (XmlSchemaSequence) xmlSchemaParticle;
////	            XmlSchemaObjectCollection xmlSchemaObjectCollection = xmlSchemaSequence.getItems();
////	            int count = xmlSchemaObjectCollection.getCount();
////	            for (int i = 0; i < count; i++) {
////	                XmlSchemaObject xmlSchemaObject = xmlSchemaObjectCollection.getItem(i);
////	                if (xmlSchemaObject instanceof XmlSchemaElement) {
////	                    XmlSchemaElement xmlSchemaElement = (XmlSchemaElement) xmlSchemaObject;
////	                    String elementName = xmlSchemaElement.getName();
////	                    XmlSchemaType xmlSchemaType = xmlSchemaElement.getSchemaType();
////	                    String elementTypeName = xmlSchemaType.getName();
////	                    if(elementTypeName!=null){
////	                        value.add(elementName);
////	                    }
////	                    search(xmlSchemaType);
////	                }
////	            }
////	        }
////	        else {//如果这层不是XmlSchemaSequence则直接获取相应的XmlSchemaAttribute如图7
////	            XmlSchemaObjectCollection xmlSchemaObjectCollection=xmlSchemaComplexType.getAttributes();
////	            int count_att = xmlSchemaObjectCollection.getCount();
////	            for (int j = 0; j < count_att; j++) {
////	                XmlSchemaObject xmlSchemaObject = xmlSchemaObjectCollection.getItem(j);
////	                if (xmlSchemaObject instanceof XmlSchemaAttribute) {
////	                    XmlSchemaAttribute xmlSchemaAttribute = (XmlSchemaAttribute) xmlSchemaObject;
////	                    String attributeName = xmlSchemaAttribute.getName();
////	                    QName xmlSchemaTypename = xmlSchemaAttribute.getSchemaTypeName();
////	                    String atttype=xmlSchemaTypename.getLocalPart();
////	                    value.add(attributeName);
////	                }
////	            }
////	        }
////	    }
////	}
//	
////	@Test
////	public void testCall(){
////		String xmlMessage = 
////		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
////		+"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" 
////		+"<soap:Body>"
////		+"<getWeatherbyCityName xmlns=\"http://WebXml.com.cn/\">"
////		+"<theCityName>深圳</theCityName>"
////		+"</getWeatherbyCityName>"
////		+"</soap:Body>"
////		+"</soap:Envelope>";
////		
////		System.out.println(xmlMessage);
////		
////		String charset = "UTF-8";
////		HttpClient httpclient = new DefaultHttpClient();
////		HttpParams httpParams = httpclient.getParams();
////		httpParams.setIntParameter("http.socket.timeout", 10000);
////		httpParams.setBooleanParameter("http.protocol.expect-continue", false);
////		HttpPost httppost = new HttpPost("http://www.webxml.com.cn/WebServices/WeatherWebService.asmx");
////		try {
////			httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
////			httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
////			httppost.setHeader("SOAPAction", "http://WebXml.com.cn/getWeatherbyCityName");
////			HttpEntity entity = new StringEntity(xmlMessage,ContentType.create("text/xml", "utf-8"));
////			httppost.setEntity(entity);
////			HttpResponse resp = httpclient.execute(httppost);
////			String retVal = EntityUtils.toString(resp.getEntity(), charset);
////			System.out.println(retVal);
////		} catch (ClientProtocolException e) {
////			e.printStackTrace();
////		} catch (ParseException e) {
////			e.printStackTrace();
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////
////	}
//	
//	@Test
//	public void testCall2(){
//		String xmlMessage = 
//		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//		+"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" 
//		+"<soap:Body>"
//		+"<getWeatherbyCityName xmlns=\"http://wspub.test.sanri/\">"
//		+"<theCityName>深圳</theCityName>"
//		+"</getWeatherbyCityName>"
//		+"</soap:Body>"
//		+"</soap:Envelope>";
//		
//		System.out.println(xmlMessage);
//		
//		String charset = "UTF-8";
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpParams httpParams = httpclient.getParams();
//		httpParams.setIntParameter("http.socket.timeout", 10000);
//		httpParams.setBooleanParameter("http.protocol.expect-continue", false);
////		httpParams.setParameter("Content-Type", "text/xml; charset=utf-8");
////		httpParams.setParameter("SOAPAction", "http://www.webxml.com.cn/getWeatherbyCityName");
//		HttpPost httppost = new HttpPost("http://localhost:8089/ws/getWeatherbyCityName");
//		try {
//			HttpEntity entity = new StringEntity(xmlMessage,ContentType.TEXT_XML);
//			httppost.setEntity(entity );
//			httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
//			httppost.setHeader("SOAPAction", "http://localhost:8089/ws/");
//			httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
//			HttpResponse resp = httpclient.execute(httppost);
//			String retVal = EntityUtils.toString(resp.getEntity(), charset);
//			System.out.println(retVal);
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
//	
//}
