package com.apj.platform.commons.vo;

import java.util.ArrayList;
import java.util.List;

public abstract class SystemRuntimeException extends RuntimeException {
	public List<Object> params;

	public abstract String getErrorcode();

	public void addToParams(Object value) {
		if (null == params) {
			params = new ArrayList<>();
		}
		params.add(value);
	}

	public SystemRuntimeException(String message) {
		super(message);
	}

}
