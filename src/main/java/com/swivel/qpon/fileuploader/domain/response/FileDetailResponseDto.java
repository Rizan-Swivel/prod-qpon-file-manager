package com.swivel.qpon.fileuploader.domain.response;

import com.swivel.qpon.fileuploader.domain.entity.FileManager;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FileDetailResponseDto implements ResponseDto {

    private final String url;
    private final String contentType;
    private final long byteSize;
    private final String name;
    private final String description;
    private final Date createdAt;
    private final Date updatedAt;

    public FileDetailResponseDto(FileManager fileManager) {
        this.url = fileManager.getUrl();
        this.contentType = fileManager.getContentType();
        this.byteSize = fileManager.getFileSize();
        this.name = fileManager.getName();
        this.description = fileManager.getDescription();
        this.createdAt = fileManager.getCreatedAt();
        this.updatedAt = fileManager.getUpdatedAt();
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
