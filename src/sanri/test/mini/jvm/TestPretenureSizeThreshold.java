package sanri.test.mini.jvm;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-6-29下午3:40:02<br/>
 * 功能: 大对象直接进入老年代 
 * VM Args -verbose:gc -Xms20m -Xmx20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8
 * -XX:PretenureSizeThreshold=3145728		//这个以字节为单位 3M
 * -XX:+UseSerialGC 需要加 serialGC 才生效,默认是 UseParallelGC
 *  <br/>
 */
public class TestPretenureSizeThreshold {
	public static void main(String[] args) {
		byte [] allocation = new byte[4 * TestAllocation._1M];		//直接分配一个 4M 的 byte 数组 
	}
}
