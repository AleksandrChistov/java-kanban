package ru.yandex.practicum.task.http;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.typeTokens.TasksTypeToken;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.http.errors.ErrorResponse;
import ru.yandex.practicum.task.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTasksTest extends HttpTaskManagerTest {
    private final String baseUrl = "http://localhost:8080/tasks/";

    @Test
    void getTasks() throws IOException, InterruptedException {
        Task task = new Task("Test get tasks", "Testing task", TaskStatus.NEW, LocalDateTime.now(), 5);
        manager.createTask(task);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> parsed = gson.fromJson(response.body(), new TasksTypeToken().getType());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(manager.getAllTasks().size(), parsed.size(), "Некорректное количество задач");
        assertEquals(task.getName(), parsed.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        Task task = new Task("Test add task", "Testing task", TaskStatus.NEW, LocalDateTime.now(), 5);

        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");

        List<Task> tasks = manager.getAllTasks();

        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals(task.getName(), tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test get task by ID", "Testing task", TaskStatus.NEW, LocalDateTime.now(), 5);
        Task created = manager.createTask(task);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + created.getId()))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task parsed = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(created.getName(), parsed.getName(), "Некорректное имя задачи");
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test delete task by ID", "Testing task", TaskStatus.NEW, LocalDateTime.now(), 5);
        Task created = manager.createTask(task);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + created.getId()))
                .header("Accept", "application/json;charset=utf-8")
                .DELETE()
                .build();

        assertEquals(1, manager.getAllTasks().size(), "Некорректное количество задач перед удалением");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals(0, manager.getAllTasks().size(), "Некорректное количество задач после удаления");
    }

    @Test
    void getTaskByIdNotFound() throws IOException, InterruptedException {
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
    void addTaskWithEmptyBody() throws IOException, InterruptedException {
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
    void addTaskWithIncorrectBody() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"Incorrect task\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Неверное тело запроса", error.message, "Сообщение об ошибке не совпадает");
    }

    @Test
    void addTaskWithIntersectedTime() throws IOException, InterruptedException {
        Task task = new Task("Test task with intersected time", "Testing task", TaskStatus.NEW, LocalDateTime.now(), 10);
        Task task2 = new Task("Test task 2 with intersected time", "Testing task", TaskStatus.NEW, LocalDateTime.now(), 5);
        manager.createTask(task);

        String task2Json = gson.toJson(task2);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Задача пересекается с другой задачей по времени", error.message, "Сообщение об ошибке не совпадает");
    }

    @Test
    void deleteTaskNotFound() throws IOException, InterruptedException {
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