package com.yd.spring.wechatCenter.model.exception;

import com.yd.spring.wechatCenter.model.result.Res;
import com.yd.spring.wechatCenter.model.result.ResBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class RestExceptionHandler extends Exception {
	private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

	private String applicationPath = "com.yd.spring";

	@ResponseBody
	@ExceptionHandler(Exception.class)
	public ResBody exceptionHandler(Exception e) {
		logger.error(e.getMessage());
		e.setStackTrace(Arrays.stream(e.getStackTrace())
				.filter(se -> se.getClassName().startsWith(applicationPath))
				.collect(toList())
				.toArray(new StackTraceElement[0]));
		if (e.getStackTrace().length > 0) {
			e.printStackTrace();
		}

		return Res.error(e.getMessage());
	}
}
