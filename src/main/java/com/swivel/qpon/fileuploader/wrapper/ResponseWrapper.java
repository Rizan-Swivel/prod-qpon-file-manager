package com.swivel.qpon.fileuploader.wrapper;

import com.swivel.qpon.fileuploader.domain.response.ResponseDto;
import com.swivel.qpon.fileuploader.domain.BaseDto;
import com.swivel.qpon.fileuploader.enums.ResponseStatusType;
import lombok.Getter;

/**
 * ResponseWrapper
 */
@Getter
public class ResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private ResponseDto data;

    public ResponseWrapper(ResponseStatusType statusType, String message, ResponseDto data) {
        this.status = statusType;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
