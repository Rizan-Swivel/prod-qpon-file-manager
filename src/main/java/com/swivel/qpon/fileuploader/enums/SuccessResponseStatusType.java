package com.swivel.qpon.fileuploader.enums;


import lombok.Getter;

/**
 * Success Response Status Type
 */
@Getter
public enum  SuccessResponseStatusType {

    IMAGE_UPLOAD (2000,"Successfully generated the image url."),
    IMAGE_DELETE( 2001,"Successfully deleted the image."),
    FILE_UPLOAD(2002,"Successfully uploaded file(s)"),
    FILE_SUMMARY(2003, "Successfully returned file list."),
    DELETE_FILE(2004, "Successfully deleted the file"),
    UPDATE_FILE(2005, "Successfully updated the file"),
    FILE_DETAIL(2006, "Successfully returned the file detail.");

    private final String code;
    private final String message;

    SuccessResponseStatusType(int code, String message) {
        this.code = getCodeString(code);
        this.message = message;
    }

    /**
     * Return String value of code to match with resource-message property key
     *
     * @param code code
     * @return code
     */
    public String getCodeString(int code) {
        return String.valueOf(code);
    }
}
