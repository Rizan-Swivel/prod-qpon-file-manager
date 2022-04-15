package com.swivel.qpon.fileuploader.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum FileTypes {

    TEXT("text"),
    APP("application"),
    IMAGE("image"),
    AUDIO("audio"),
    VIDEO("video"),
    PDF("pdf");

    private final String fileType;

    FileTypes(String fileType) {
        this.fileType = fileType;
    }

    /**
     * This method check for valid file options
     *
     * @param fileType fileType
     * @return true/false
     */
    public static boolean isValidFileType(String fileType) {
        if (fileType != null) {
            for (FileTypes f : FileTypes.values()) {
                if (f.toString().equalsIgnoreCase(fileType.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method return the file types
     *
     * @return List<String>
     */
    public static List<String> getFileTypes() {
        List<String> types = new ArrayList<>();
        for (FileTypes fileTypes : FileTypes.values()) {
            types.add(fileTypes.getFileType());
        }
        return types;
    }

}
