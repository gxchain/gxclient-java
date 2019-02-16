package com.gxchain.client.exception;

public class HttpAccessFailException extends RuntimeException {

    public HttpAccessFailException() {
        super();
    }

    public HttpAccessFailException(String message) {
        super(message);
    }

    public HttpAccessFailException(Throwable cause) {
        super(cause);
    }


    public HttpAccessFailException(String message, Throwable cause) {
        super(message, cause);
    }

}
