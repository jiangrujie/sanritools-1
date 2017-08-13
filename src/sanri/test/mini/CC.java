package sanri.test.mini;

public class CC {
	public static void main(String[] args) {
		new CC().a();
	}
	
	public void a(){
		try {
			te();
			normal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void te(){
		try {
			for(int i=0;i<10;i++){
				test2();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("继续运行");
	}
	
	
	public void test2(){
		for(int i=0;i<100;i++){
			if(i == 10){
				throw new RuntimeException("出错");
			}
			System.out.println("当前记录"+i);
		}
	}
	
	public void normal(){
		for (int i = 0; i < 10; i++) {
			System.out.println("记录 b");
		}
	}
}
