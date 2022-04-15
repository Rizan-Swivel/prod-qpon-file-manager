package com.swivel.qpon.fileuploader.domain.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ImageResponseDto implements ResponseDto {

    private String imageUrl;
    private String contentType;
    private long imageByteSize;

    public ImageResponseDto(MultipartFile multipartFile, String imageUrl) {
        this.contentType = multipartFile.getContentType();
        this.imageByteSize = multipartFile.getSize();
        this.imageUrl = imageUrl;
    }


    @Override
    public String toLogJson() {
        return toJson();
    }
}
