package sanri.utils.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-8-12上午11:45:39<br/>
 * 功能:导入导出列配置,注解加到属性上说明对于导入和导出的配置是一样的,
 * 加到 set 上只针对导入,get 上只针对导出,get,set 上的配置会覆盖属性上的配置 <br/>
 */
@Target(value={ElementType.METHOD,ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {
	/**
	 * 作者:sanri <br/>
	 * 时间:2017-8-12上午11:47:56<br/>
	 * 功能:导出,导入的单元格标题 ,必填<br/>
	 * @return
	 */
	String value() ;
	/**
	 * 作者:sanri <br/>
	 * 时间:2017-8-12上午11:49:29<br/>
	 * 功能:导入,导出时的索引配置,从 0 开始,必须提供 <br/>
	 * @return
	 */
	int index();
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-12下午12:23:19<br/>
	 * 功能: 列的宽度配置,如果这里有配置,则使用这里的配置,否则使用自动宽度(如果配置为 true 的话,为 false 不设置)<br/>
	 * @return
	 */
	int width() default -1;
	
	/**
	 * 
	 * 功能:由于自动宽度对中文支持不太好,所以这里加个中文的自动宽度支持,这个只在自动宽度设置为 true 时生效<br/>
	 * 创建时间:2017-8-13上午8:35:54<br/>
	 * 作者：sanri<br/>
	 * @return<br/>
	 */
	boolean chineseWidth() default false;
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-12下午12:26:47<br/>
	 * 功能: 当前列是否隐藏 默认 false<br/>
	 * @return
	 */
	boolean hidden() default false;
	
	/**
	 * 
	 * 作者:sanri <br/>
	 * 时间:2017-8-12下午1:46:24<br/>
	 * 功能: 时间格式化,默认 yyyy-MM-dd <br/>
	 * @return
	 */
	String pattern() default "yyyy-MM-dd";
}
