package com.swivel.qpon.fileuploader.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto for image request
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequestDto extends RequestDto {

    String imageUrl;

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(imageUrl);
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
