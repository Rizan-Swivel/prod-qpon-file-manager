package com.swivel.qpon.fileuploader.domain.entity;

import com.swivel.qpon.fileuploader.domain.FileManagerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "file")
@AllArgsConstructor
@NoArgsConstructor
public class FileManager implements Serializable {

    @Id
    private String id;
    private String name;
    private String description;
    private String url;
    @Column(name = "user_id")
    private String userId;
    private String contentType;
    private long fileSize;
    @Column(name = "createdAt")
    private Date createdAt;
    @Column(name = "updatedAt")
    private Date updatedAt;
    private String type;

    public FileManager(FileManagerDto fileManagerDto, String fileId) {
        this.id = fileId;
        this.name = fileManagerDto.getFileName();
        this.url = fileManagerDto.getUrl();
        this.userId = fileManagerDto.getUserId();
        this.contentType = fileManagerDto.getContentType();
        this.fileSize = fileManagerDto.getFileSize();
        this.type = fileManagerDto.getType();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}
