package ru.yandex.practicum.task.http;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.typeTokens.TasksTypeToken;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.http.errors.ErrorResponse;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Subtask;
import ru.yandex.practicum.task.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerSubtasksTest extends HttpTaskManagerTest {
    private final String baseUrl = "http://localhost:8080/subtasks/";

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic get", "Epic testing", TaskStatus.NEW);
        final Epic createdEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test get subtasks", "Test subtask", TaskStatus.NEW, createdEpic.getId(), LocalDateTime.now(), 0);

        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> parsed = gson.fromJson(response.body(), new TasksTypeToken().getType());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(manager.getAllSubtasks().size(), parsed.size(), "Некорректное количество подзадач");
        assertEquals(subtask.getName(), parsed.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    void addSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic add", "Epic testing", TaskStatus.NEW);
        final Epic createdEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test add subtask", "Test subtask", TaskStatus.NEW, createdEpic.getId(), LocalDateTime.now(), 0);

        String taskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");

        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertEquals(subtask.getName(), subtasks.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic get by ID", "Epic testing", TaskStatus.NEW);
        final Epic createdEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test get subtask by ID", "Test subtask", TaskStatus.NEW, createdEpic.getId(), LocalDateTime.now(), 0);

        Subtask created = manager.createSubtask(subtask);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + created.getId()))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask parsed = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(created.getName(), parsed.getName(), "Некорректное имя подзадачи");
    }

    @Test
    void deleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic delete", "Epic testing", TaskStatus.NEW);
        final Epic createdEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test delete subtask", "Test subtask", TaskStatus.NEW, createdEpic.getId(), LocalDateTime.now(), 0);

        Subtask created = manager.createSubtask(subtask);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + created.getId()))
                .header("Accept", "application/json;charset=utf-8")
                .DELETE()
                .build();

        assertEquals(1, manager.getAllSubtasks().size(), "Некорректное количество подзадач перед удалением");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals(0, manager.getAllTasks().size(), "Некорректное количество подзадач после удаления");
    }

    @Test
    void getSubtaskByIdNotFound() throws IOException, InterruptedException {
        int id = 1;
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + id))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Задача с id = " + id + " не найдена", error.message, "Сообщение об ошибке не совпадает");
    }

    @Test
    void addSubtaskWithEmptyBody() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Неверное тело запроса", error.message, "Сообщение об ошибке не совпадает");
    }

    @Test
    void addSubtaskWithIncorrectBody() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"Incorrect subtask\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Неверное тело запроса", error.message, "Сообщение об ошибке не совпадает");
    }

    @Test
    void addSubtaskWithIntersectedTime() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic with intersected time", "Epic testing", TaskStatus.NEW);
        final Epic createdEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test subtask with intersected time", "Test subtask", TaskStatus.NEW, createdEpic.getId(), LocalDateTime.now(), 10);
        Subtask subtask2 = new Subtask(
                "Test subtask 2 with intersected time", "Test subtask 2", TaskStatus.NEW, createdEpic.getId(), LocalDateTime.now(), 5);

        manager.createSubtask(subtask);

        String subtask2Json = gson.toJson(subtask2);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Задача пересекается с другой задачей по времени", error.message, "Сообщение об ошибке не совпадает");
    }

    @Test
    void deleteSubtaskNotFound() throws IOException, InterruptedException {
        int id = 1;
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + id))
                .header("Accept", "application/json;charset=utf-8")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Задача с id = " + id + " не найдена", error.message, "Сообщение об ошибке не совпадает");
    }
}