package sanri.test.mini.jvm;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-6-29下午4:46:52<br/>
 * 功能:测试新生代的默认大小 
 * VM Args -XX:+PrintGCDetails -Xms30m -Xmx30m -XX:+UseSerialGC
 * 默认新生代与老年代的比值为 1:2  -XX:NewRatio=2
 * 默认幸存代和eden 区的比值为 1:1:8 -XX:SurvivorRatio=8
 *  <br/>
 */
public class TestDefault {
	
	public static void main(String[] args) {
		
	}
}
