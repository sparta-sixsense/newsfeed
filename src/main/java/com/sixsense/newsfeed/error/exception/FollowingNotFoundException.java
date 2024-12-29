package com.sixsense.newsfeed.error.exception;

import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.NotFoundException;

public class FollowingNotFoundException extends NotFoundException {

    public FollowingNotFoundException() {
        super(ErrorCode.FOLLOWING_NOT_FOUND);
    }
}
