package ru.yandex.practicum.task.error;

import java.util.Objects;

public class TimeIntersectedException extends RuntimeException {
    public TimeIntersectedException(Integer id) {
        super(String.format("Задача%s пересекается с другой задачей по времени", (Objects.isNull(id) ? "" : (" с id = " + id))));
    }
}
