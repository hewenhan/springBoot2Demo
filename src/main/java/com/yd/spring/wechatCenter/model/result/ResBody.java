package com.yd.spring.wechatCenter.model.result;

public class ResBody<T> {

	public Boolean success = false;
	public T       data;
	public String  msg     = "";

	public ResBody() {

	}

	public void setData(T dataArg) {
		data = dataArg;
	}

	public void setMsg(String msgArg) {
		if (msgArg == null) {
			msgArg = "";
		}
		msg = msgArg;
	}

	public void setSuccess() {
		success = true;
	}

	public void setFail() {
		success = false;
	}
}
