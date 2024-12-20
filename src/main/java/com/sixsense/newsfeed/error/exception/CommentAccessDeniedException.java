package com.sixsense.newsfeed.error.exception;


import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.AccessDeniedException;

public class CommentAccessDeniedException extends AccessDeniedException {

    public CommentAccessDeniedException() {
        super(ErrorCode.COMMENT_ACCESS_DENIED);
    }
}
