package com.apj.platform.commons.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiError {
    private String errorCode;
    private String errMessage;
    private Object params;
}
