package com.swivel.qpon.fileuploader.wrapper;

import com.swivel.qpon.fileuploader.domain.response.ResponseDto;
import com.swivel.qpon.fileuploader.enums.ResponseStatusType;
import com.swivel.qpon.fileuploader.enums.SuccessResponseStatusType;
import lombok.Getter;

/**
 * Success Response Wrapper
 */
@Getter
public class SuccessResponseWrapper extends ResponseWrapper {

    private final String displayMessage;

    public SuccessResponseWrapper(SuccessResponseStatusType successResponseStatusType, String message,
                                  ResponseDto data) {
        super(ResponseStatusType.SUCCESS, successResponseStatusType.getMessage(), data);
        this.displayMessage = message;
    }
}
