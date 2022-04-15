package com.swivel.qpon.fileuploader.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileVolumeResponse implements ResponseDto {

    private long total;
    private long remaining;

    @Override
    public String toLogJson() {
        return toJson();
    }

}
