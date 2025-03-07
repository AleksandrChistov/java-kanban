package ru.yandex.practicum.task.http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime != null ? localDateTime.format(formatter) : null);
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String dateStr = jsonReader.nextString();
        return dateStr != null ? LocalDateTime.parse(dateStr, formatter) : null;
    }
}
