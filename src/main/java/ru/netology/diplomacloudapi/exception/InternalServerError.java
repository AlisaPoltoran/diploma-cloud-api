package ru.netology.diplomacloudapi.exception;

import java.io.IOException;

public class InternalServerError extends IOException {
    public InternalServerError (String msg, IOException e) {
        super(msg);
    }
}
