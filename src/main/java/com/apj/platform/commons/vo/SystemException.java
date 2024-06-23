package com.apj.platform.commons.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.springframework.http.HttpStatus;

public abstract class SystemException extends Exception {
	public List<String> params;

	public abstract String getErrorcode();

	public void addToParams(String... value) {
		if (null == params) {
			params = new ArrayList<>();
		}
		params.addAll(Arrays.asList(value));
	}

	public SystemException(String message) {
		super(message);
	}

	public HttpStatus getStatusCode() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
