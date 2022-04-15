package com.swivel.qpon.fileuploader.domain.response;

import com.swivel.qpon.fileuploader.domain.entity.FileManager;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class FileResponseDto implements ResponseDto {

    private final String url;
    private final String contentType;
    private final long byteSize;
    private final String name;
    private String description;

    public FileResponseDto(MultipartFile multipartFile, String fileUrl, String name) {
        this.contentType = multipartFile.getContentType();
        this.byteSize = multipartFile.getSize();
        this.url = fileUrl;
        this.name = name;
    }

    public FileResponseDto(FileManager fileManager) {
        this.contentType = fileManager.getContentType();
        this.byteSize = fileManager.getFileSize();
        this.url = fileManager.getUrl();
        this.name = fileManager.getName();
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
