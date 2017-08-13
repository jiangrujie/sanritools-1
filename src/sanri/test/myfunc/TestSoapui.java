//package sanri.test.myfunc;
//
//import java.io.IOException;
//import java.io.StringWriter;
//import java.util.List;
//
//import javax.wsdl.BindingOperation;
//import javax.wsdl.Part;
//import javax.xml.namespace.QName;
//
//import org.apache.xmlbeans.SchemaType;
//import org.apache.xmlbeans.XmlCursor;
//import org.apache.xmlbeans.XmlException;
//import org.apache.xmlbeans.XmlObject;
//import org.junit.Test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.eviware.soapui.impl.wsdl.WsdlInterface;
//import com.eviware.soapui.impl.wsdl.WsdlOperation;
//import com.eviware.soapui.impl.wsdl.WsdlProject;
//import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
//import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter;
//import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
//import com.eviware.soapui.impl.wsdl.support.xsd.SampleXmlUtil;
//import com.eviware.soapui.model.iface.Operation;
//import com.eviware.soapui.support.xml.XmlUtils;
//
///**
// * 
// * 作者:sanri <br/>
// * 时间:2017-6-15下午6:21:29<br/>
// * 功能:使用 soapui 解析 wsdl  <br/>
// */
//public class TestSoapui {
//	
//	/**
//	 * 
//	 * 作者:sanri <br/>
//	 * 时间:2017-6-15下午6:21:48<br/>
//	 * 功能:soapui <br/>
//	 */
//	@Test
//	public void testParser(){
//		ImportWsdl importWsdl = new ImportWsdl();
//		try {
//			String wsdlURL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
//			List<WsMethodInfo> proBySoap = importWsdl.getProBySoap(wsdlURL);
//			System.out.println(JSONObject.toJSONString(proBySoap));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void testP(){
//		try {
//			WsdlImporter wsdlImporter = WsdlImporter.getInstance();
//			WsdlProject wsdlProject = new WsdlProject();
//			String wsdlUrl = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
//			WsdlInterface[] importWsdl = wsdlImporter.importWsdl(wsdlProject, wsdlUrl);
//			WsdlInterface wsdlInterface = importWsdl[0];
//			String portName = wsdlInterface.getName();
//			System.out.println("portName:"+portName);
//			List<Operation> operations = wsdlInterface.getOperations();
//			for (Operation operation : operations) {
//				String operationName = operation.getName();
//				System.out.println("方法名称 ："+operationName);
//				WsdlOperation wsdlOperation = (WsdlOperation) operation;
////				String createRequest = wsdlOperation.createRequest(true);
////				System.out.println(createRequest);
//				WsdlContext wsdlContext = wsdlInterface.getWsdlContext();
//				BindingOperation bindingOperation = wsdlOperation.findBindingOperation(wsdlContext.getDefinition());
//				boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded(bindingOperation);
//				SampleXmlUtil xmlGenerator = new SampleXmlUtil(inputSoapEncoded);
//				xmlGenerator.setIgnoreOptional(false);//与 createRequest 那相反
//			    XmlObject object = org.apache.xmlbeans.XmlObject.Factory.newInstance();
//			    XmlCursor cursor = object.newCursor();
//				Part inputParts[] = WsdlUtils.getInputParts(bindingOperation);
//				for (Part part : inputParts) {
//					QName typeName = part.getTypeName();
//					SchemaType schemaType = wsdlContext.findType(typeName);
//					xmlGenerator.createSampleForType(schemaType, cursor);
//				}
//				cursor.dispose();
//				StringWriter stringWriter = new StringWriter();
//				XmlUtils.serializePretty(object, stringWriter);
////				System.out.println(stringWriter.toString());
//				stringWriter.close();
//				System.out.println(object.xmlText());
//				break;
//			}
//		} catch (XmlException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
