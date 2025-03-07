package ru.yandex.practicum.task.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String id) {
        super("Задача с id = " + id + " не найдена");
    }
}
