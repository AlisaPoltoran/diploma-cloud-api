package ru.netology.diplomacloudapi.exception;

public class ErrorInputData extends RuntimeException {
    public ErrorInputData (String msg) {
        super(msg);
    }

}
