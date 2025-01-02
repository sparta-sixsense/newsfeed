package com.sixsense.newsfeed.error.exception.base;


import com.sixsense.newsfeed.error.ErrorCode;

public class EntityAlreadyExistsException extends BusinessBaseException {

    public EntityAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityAlreadyExistsException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
