package com.swivel.qpon.fileuploader.enums;

import lombok.Getter;

/**
 * Error response status type
 */
@Getter
public enum ErrorResponseStatusType {

    MISSING_REQUIRED_FIELDS(4000, "Required fields are missing"),
    INVALID_IMAGE_TYPE(4901, "Invalid image type"),
    EXCEEDED_IMAGE_SIZE(4902, "Image exceeded maximum byte size"),
    INVALID_IMAGE_URL(4903, "Invalid image url"),
    UNSUPPORTED_FILE_FORMAT(4014, "Unsupported file format"),
    EXCEEDED_FILE_SIZE(4905, "File exceeded maximum byte size"),
    INTERNAL_SERVER_ERROR(5000, "Failed due to an internal server error"),
    INVALID_UPLOAD_NAME(4906, "Unsupported image name."),
    MAX_FILE_COUNT(4015, "Maximum file count exceeded."),
    UNSUPPORTED_FILE_TYPE(4016, "Unsupported file option."),
    INVALID_FILE_ID(4017, "Invalid file Id");
    private final int code;
    private final String message;

    ErrorResponseStatusType(int code, String message) {
        this.code = code;
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
