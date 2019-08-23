package com.exemple.exception;

import com.exemple.model.SlimpayErrorCode;

public class SlimpayClientException extends Exception {

    private Integer httpStatusCode;
    private SlimpayErrorCode slimpayErrorCode;

    public SlimpayClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public SlimpayClientException setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
        return this;
    }

    public SlimpayErrorCode getSlimpayErrorCode() {
        return slimpayErrorCode;
    }

    public SlimpayClientException setSlimpayErrorCode(SlimpayErrorCode slimpayErrorCode) {
        this.slimpayErrorCode = slimpayErrorCode;
        return this;
    }
}
