package ru.netology.diplomacloudapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse implements Response {

    private int id;
    private String msg;

}
