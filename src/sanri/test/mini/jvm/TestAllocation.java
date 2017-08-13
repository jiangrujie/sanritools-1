package sanri.test.mini.jvm;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-6-12下午3:14:23<br/>
 * 功能:测试内存分配  <br/>
 * VM Args: -Xms20m -Xmx20m -Xmn10m -XX:+PrintGCDetails -XX:+UseSerialGC
 * 这里没有配置 survivor 区与 eden 区的比例,默认是 2:8 即 10 份内存 from,to 各占一份, eden 区占 8 份
 * 
 * -XX:+TraceClassLoading 打印类加载过程,但类不是加载到方法区吗
 */
public class TestAllocation {
	
	public final static int _1M = 1024*1024;
	public static void main(String[] args) {
		byte [] a = new byte[2 * _1M];
		byte [] b = new byte[2 * _1M];
		byte [] c = new byte[2 * _1M];
		byte [] d = new byte[4 * _1M];
	}
}
