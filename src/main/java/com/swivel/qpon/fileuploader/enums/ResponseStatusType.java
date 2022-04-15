package com.swivel.qpon.fileuploader.enums;

/**
 * ResponseStatusType
 */
public enum ResponseStatusType {

    SUCCESS("Success"),
    ERROR("Error"),
    UNAUTHORISED("Unauthorised"),
    FORBIDDEN("Forbidden");

    private final String status;

    ResponseStatusType(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }
}
