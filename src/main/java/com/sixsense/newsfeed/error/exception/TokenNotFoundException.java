package com.sixsense.newsfeed.error.exception;


import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.NotFoundException;

public class TokenNotFoundException extends NotFoundException {
    public TokenNotFoundException() {
        super(ErrorCode.TOKEN_NOT_FOUND);
    }
}
