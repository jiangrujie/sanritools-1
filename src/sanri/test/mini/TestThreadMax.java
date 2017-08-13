package sanri.test.mini;

public class TestThreadMax {
	public static void main(String[] args) {
		int i =0;
		while(true){
			i++;
			System.out.println("当前第 :" + i +" 个线程");
		    new Thread(new Runnable(){
		        public void run() {
		            try {
		                Thread.sleep(10000000);
		            } catch(InterruptedException e) { }        
		        }    
		    }).start();
		}
	}
}
