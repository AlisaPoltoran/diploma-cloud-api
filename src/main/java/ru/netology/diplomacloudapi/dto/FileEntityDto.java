package ru.netology.diplomacloudapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.netology.diplomacloudapi.entity.FileEntity;

@AllArgsConstructor
@Getter
@Setter
public class FileEntityDto {
    @JsonProperty("filename")
    private String fileName;
    @JsonProperty("size")
    private int sizeInBytes;

    public FileEntityDto(FileEntity fileEntity) {
        this.fileName = fileEntity.getName();
        this.sizeInBytes = fileEntity.getSizeInBytes();
    }
}
