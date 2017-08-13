package sanri.test.rmi.jdk;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerBind {
	public static void main(String[] args) {
		// 注册管理器
		Registry registry = null;
		try {
			// 创建一个服务注册管理器
			registry = LocateRegistry.createRegistry(8088);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
			// 创建一个服务
			IService server = new ServiceImpl();
			// 将服务绑定命名
			registry.rebind("vince", server);

			System.out.println("bind server");
		} catch (RemoteException e) {

		}
	}
}
