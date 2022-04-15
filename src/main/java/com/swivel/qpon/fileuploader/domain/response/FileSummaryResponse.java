package com.swivel.qpon.fileuploader.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FileSummaryResponse implements ResponseDto {

    private FileVolumeResponse volume;
    private List<String> fileTypes;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
