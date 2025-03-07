package ru.yandex.practicum.task.http;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.typeTokens.TasksTypeToken;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskHistoryTest extends HttpTaskManagerTest {
    private final String baseUrl = "http://localhost:8080/history/";

    @Test
    void getHistoryTasks() throws IOException, InterruptedException {
        Task task = new Task("Test get history tasks", "Testing task", TaskStatus.NEW, LocalDateTime.now(), 5);
        Task created = manager.createTask(task);

        manager.getTask(created.getId());

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> parsed = gson.fromJson(response.body(), new TasksTypeToken().getType());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(manager.getHistory().size(), parsed.size(), "Некорректное количество задач");
        assertEquals(task.getName(), parsed.getFirst().getName(), "Некорректное имя задачи");
    }

}