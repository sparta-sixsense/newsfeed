package com.sixsense.newsfeed.error.exception;


import com.sixsense.newsfeed.error.ErrorCode;
import com.sixsense.newsfeed.error.exception.base.AccessDeniedException;

public class ScheduleAccessDeniedException extends AccessDeniedException {

    public ScheduleAccessDeniedException() {
        super(ErrorCode.SCHEDULE_ACCESS_DENIED);
    }
}
