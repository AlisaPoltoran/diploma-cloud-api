package ru.netology.diplomacloudapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewFileName {
    @JsonProperty("filename")
    private String fileName;
}
