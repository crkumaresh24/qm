package com.apj.platform.commons.beans;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import com.apj.platform.commons.vo.ApiError;
import com.apj.platform.commons.vo.SystemException;
import com.apj.platform.qm.v1.constants.QmErrorCodes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    private final MessageSource messageSource;

    public ControllerExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleValidationExceptions(
            MethodArgumentNotValidException ex, Locale locale) {
        ApiError apiError = new ApiError();
        apiError.setErrorCode(QmErrorCodes.ERR_INPUT_VALIDATION);
        apiError.setErrMessage(messageSource.getMessage(apiError.getErrorCode(), null, locale));
        Map<String, ApiError> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            ApiError se = new ApiError();
            se.setErrorCode(error.getDefaultMessage());
            se.setErrMessage(messageSource.getMessage(se.getErrorCode(), null, locale));
            errors.put(fieldName, se);
        });
        apiError.setParams(errors);
        return apiError;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MultipartException.class)
    public ApiError handleMultiPartExceptions(
            MultipartException ex, Locale locale) {
        log.error(ex.getMessage(), ex);
        ApiError apiError = new ApiError();
        apiError.setErrorCode(QmErrorCodes.ERR_INPUT_VALIDATION);
        apiError.setErrMessage(messageSource.getMessage(QmErrorCodes.ERR_MULTIPART_NOTFOUND, null, locale));
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        apiError.setParams(errors);
        return apiError;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    public ApiError handleInvalidDateExceptions(
            DateTimeParseException ex, Locale locale) {
        ApiError apiError = new ApiError();
        apiError.setErrorCode(QmErrorCodes.ERR_INPUT_VALIDATION);
        apiError.setErrMessage(messageSource.getMessage(apiError.getErrorCode(), null, locale));
        Map<String, ApiError> errors = new HashMap<>();
        errors.put("date", apiError);
        apiError.setParams(errors);
        return apiError;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiError> handleExceptions(
            SystemException ex, Locale locale) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(resolveApiErrorFromMessage(ex.getErrorcode(), ex, locale));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BadCredentialsException.class)
    public ApiError handleExceptions(
            BadCredentialsException ex, Locale locale) {
        return resolveApiErrorFromMessage(QmErrorCodes.ERR_BAD_CREDENTIALS, ex, locale);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiError handleMissingRequireParametersDateExceptions(
            MissingServletRequestParameterException ex, Locale locale) {
        return resolveApiErrorFromMessage(QmErrorCodes.ERR_INPUT_VALIDATION, ex, locale);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BadSqlGrammarException.class)
    public ApiError handleBadSQLExceptions(
            BadSqlGrammarException ex, Locale locale) {
        return resolveApiErrorFromMessage(QmErrorCodes.ERR_QUERY_INVALID, ex, locale);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleException(
            Exception ex, Locale locale) {
        ApiError apiError = new ApiError();
        apiError.setErrorCode(QmErrorCodes.ERR_GENRIC);
        apiError.setErrMessage("generic error");
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        apiError.setParams(errors);
        log.error("generic error", ex);
        return apiError;
    }

    private ApiError resolveApiErrorFromMessage(String errorCode, Exception e, Locale locale) {
        ApiError apiError = new ApiError();
        apiError.setErrorCode(errorCode);
        apiError.setParams(new Object[] {
                e.getMessage()
        });
        if (e instanceof SystemException) {
            SystemException ex = (SystemException) e;
            if (null != ex.params) {
                apiError.setParams(ex.params.toArray());
            }
        }
        try {
            apiError.setErrMessage(
                    messageSource.getMessage(apiError.getErrorCode(), (Object[]) apiError.getParams(), locale));
        } catch (Exception ne) {
            apiError.setErrMessage("generic error");
        }
        log.error(apiError.toString());
        log.debug(apiError.toString(), e);
        return apiError;
    }
}
