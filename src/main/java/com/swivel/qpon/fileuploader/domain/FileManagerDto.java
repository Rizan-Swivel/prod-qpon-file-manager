package com.swivel.qpon.fileuploader.domain;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileManagerDto {

    private final String fileName;
    private String description;
    private final String url;
    private final String userId;
    private final String contentType;
    private final long fileSize;
    private final String type;

    public FileManagerDto(ObjectMetadata fileMetadata, String url, String fileName, String userId) {
        this.fileName = fileName;
        this.userId = userId;
        this.contentType = fileMetadata.getContentType();
        this.fileSize = fileMetadata.getContentLength();
        this.url = url;
        this.type = fileMetadata.getContentType().split("/")[0];
    }

}