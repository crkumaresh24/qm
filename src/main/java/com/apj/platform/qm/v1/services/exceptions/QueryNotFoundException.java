package com.apj.platform.qm.v1.services.exceptions;

import org.springframework.http.HttpStatus;

import com.apj.platform.commons.vo.SystemException;
import com.apj.platform.qm.v1.constants.QmErrorCodes;

public class QueryNotFoundException extends SystemException {

    public QueryNotFoundException(Long id) {
        super(String.valueOf(id));
        addToParams(String.valueOf(id));
    }

    @Override
    public String getErrorcode() {
        return QmErrorCodes.ERR_QUERY_NOT_FOUND;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }
}
