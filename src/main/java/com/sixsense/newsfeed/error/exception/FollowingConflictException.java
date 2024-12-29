package com.sixsense.newsfeed.error.exception;

import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.ConflictException;

public class FollowingConflictException extends ConflictException {

    public FollowingConflictException() {
        super(ErrorCode.FOLLOWING_CONFLICT);
    }
}
