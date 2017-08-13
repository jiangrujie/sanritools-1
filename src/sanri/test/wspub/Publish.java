package sanri.test.wspub;

import javax.xml.ws.Endpoint;

public class Publish {
	public static void main(String[] args) {
		ABC abc = new AbcImpl();
		Endpoint.publish("http://localhost:8089/ws", abc);
		System.out.println("发布成功");
	}
}
