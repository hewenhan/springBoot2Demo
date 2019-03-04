package com.yd.spring.springBoot2Demo.model.result;

import java.util.HashMap;

public class Res {

	public Res() {

	}

	public static ResBody success() {
		ResBody result = new ResBody();
		result.setSuccess();
		result.setData(new HashMap());
		return result;
	}

	public static <T> ResBody success(T successData) {
		ResBody result = new ResBody();
		result.setSuccess();
		result.setData(successData);
		return result;
	}

	public static ResBody error(String errorMsg) {
		ResBody result = new ResBody();
		result.setFail();
		result.setData(new HashMap());
		result.setMsg(errorMsg);
		return result;
	}
}
