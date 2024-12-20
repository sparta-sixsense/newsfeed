package com.sixsense.newsfeed.error.exception;


import com.sixsense.newsfeed.error.exception.base.NotFoundException;

import static com.sixsense.newsfeed.error.ErrorCode.COMMENT_NOT_FOUND;

public class CommentNotFoundException extends NotFoundException {

    public CommentNotFoundException() {
        super(COMMENT_NOT_FOUND);
    }
}
