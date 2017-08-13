package com.sanri.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sanri.frame.HandlerExceptionResolver;
import com.sanri.frame.ModelAndView;

/**
 * 
 * 作者:sanri <br/>
 * 时间:2017-4-20上午10:13:42<br/>
 * 功能:异常处理类 <br/>
 */
public class ExceptionHandler implements HandlerExceptionResolver{
	private Log logger = LogFactory.getLog(getClass());
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
		ModelAndView modelAndView = new ModelAndView();
		logger.error(ex.getMessage());
		ex.printStackTrace();
		return modelAndView;
	}

}
