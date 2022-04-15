package com.swivel.qpon.fileuploader.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileCountResponse implements ResponseDto {

    private long total;
    private long text;
    private long application;
    private long audio;
    private long video;
    private long image;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
