package com.sixsense.newsfeed.error.exception;


import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.ConflictException;

public class UserConflictException extends ConflictException {

    public UserConflictException() {
        super(ErrorCode.USER_CONFLICT);
    }
}
