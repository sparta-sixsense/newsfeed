package com.sixsense.newsfeed.error.exception;


import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.InvalidValueException;

public class InvalidTokenException extends InvalidValueException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
