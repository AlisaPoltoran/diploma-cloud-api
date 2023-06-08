package ru.netology.diplomacloudapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewFileName {
    @JsonProperty("filename")
    private String fileName;
}
