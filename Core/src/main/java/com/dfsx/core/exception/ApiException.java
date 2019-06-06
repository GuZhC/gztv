package com.dfsx.core.exception;


/**
 * Created by zr on 2015-03-27.
 */
public class ApiException extends Exception {
    public ApiException() {
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    public ApiException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ApiException(Throwable throwable) {
        super(throwable);
    }

    public static ApiException exception(Exception e) {
        return new ApiException(e);
    }

    public static ApiException error(String message) { return new ApiException(message); }


}