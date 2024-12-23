package com.sixsense.newsfeed.domain;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("active"),
    INACTIVE("inactive"),
    ;

    private final String status;

    Status(String status) {
        this.status = status;
    }
}
