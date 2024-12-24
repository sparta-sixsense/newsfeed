package com.sixsense.newsfeed.domain;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DELETED("delete")
    ;

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == ACTIVE.status;
    }
}
