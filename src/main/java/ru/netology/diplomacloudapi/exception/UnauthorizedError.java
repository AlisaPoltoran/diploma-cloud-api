package ru.netology.diplomacloudapi.exception;

public class UnauthorizedError extends RuntimeException{
    public UnauthorizedError (String msg) {
        super(msg);
    }
}
