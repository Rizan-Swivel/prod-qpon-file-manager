package com.swivel.qpon.fileuploader.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUpdateRequestDto extends RequestDto {

    private String fileId;
    private String name;
    private String description;

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(fileId) && isNonEmpty(name);
    }

    @Override
    public String toLogJson() {
        return toJson();
    }

}
