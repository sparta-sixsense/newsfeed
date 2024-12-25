package com.sixsense.newsfeed.error.exception;

import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.AccessDeniedException;

// AccessDeniedException 계열로 가도 되는 건지
public class UserInactiveOrDeletedException extends AccessDeniedException {

    public UserInactiveOrDeletedException() {
        super(ErrorCode.USER_INACTIVE_OR_DELETED);
    }
}
