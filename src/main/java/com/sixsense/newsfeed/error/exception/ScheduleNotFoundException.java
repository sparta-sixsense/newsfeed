package com.sixsense.newsfeed.error.exception;


import com.sixsense.newsfeed.error.exception.base.NotFoundException;

import static com.sixsense.newsfeed.error.ErrorCode.SCHEDULE_NOT_FOUND;

public class ScheduleNotFoundException extends NotFoundException {

    public ScheduleNotFoundException() {
        super(SCHEDULE_NOT_FOUND);
    }
}
