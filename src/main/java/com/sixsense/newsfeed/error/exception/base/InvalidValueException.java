package com.sixsense.newsfeed.error.exception.base;


import com.sixsense.newsfeed.error.ErrorCode;

public class InvalidValueException extends BusinessBaseException {

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidValueException() {
        super(ErrorCode.INVALID_INPUT_VALUE);
    }
}
