package com.swivel.qpon.fileuploader.domain.response;

import com.swivel.qpon.fileuploader.domain.entity.FileManager;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FileListPageResponse implements ResponseDto {

    private final List<FileManager> files = new ArrayList<>();

    public FileListPageResponse(List<FileManager> fileResponseDtoList) {
        files.addAll(fileResponseDtoList);
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
