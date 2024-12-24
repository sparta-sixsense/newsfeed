package com.sixsense.newsfeed.domain;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("active"),
<<<<<<< HEAD
    INACTIVE("inactive");
=======
    INACTIVE("inactive"),
    ;
>>>>>>> main

    private final String status;

    Status(String status) {
        this.status = status;
    }
}
