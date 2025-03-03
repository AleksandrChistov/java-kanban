package ru.yandex.practicum.task.http.errors;

public class ErrorResponse {
    public String message;
    public int code;

    public ErrorResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
