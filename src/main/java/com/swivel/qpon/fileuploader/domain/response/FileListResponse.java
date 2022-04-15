package com.swivel.qpon.fileuploader.domain.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FileListResponse implements ResponseDto {

    private final List<FileResponseDto> files = new ArrayList<>();

    public FileListResponse (List<FileResponseDto> fileResponseDtos) {
        files.addAll(fileResponseDtos);
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
