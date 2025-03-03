package ru.yandex.practicum.task.http;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.typeTokens.SubtasksTypeToken;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerEpicsTest extends HttpTaskManagerTest {
    private final String baseUrl = "http://localhost:8080/epics/";

    @Test
    void getEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic gets", "Epic testing", TaskStatus.NEW);
        manager.createEpic(epic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> parsed = gson.fromJson(response.body(), new TasksTypeToken().getType());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(manager.getAllEpics().size(), parsed.size(), "Некорректное количество эпиков");
        assertEquals(epic.getName(), parsed.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic add", "Epic testing", TaskStatus.NEW);
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");

        List<Epic> epics = manager.getAllEpics();

        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals(epic.getName(), epics.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic get by ID", "Epic testing", TaskStatus.NEW);
        final Epic createdEpic = manager.createEpic(epic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + createdEpic.getId()))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic parsed = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(createdEpic.getName(), parsed.getName(), "Некорректное имя эпика");
    }

    @Test
    void getSubtasksByEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic get subtasks by epic", "Epic testing", TaskStatus.NEW);
        final Epic createdEpic = manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "Test get subtasks by epic", "Test subtask", TaskStatus.NEW, createdEpic.getId(), LocalDateTime.now(), 0);
        Subtask creaetedSubtask = manager.createSubtask(subtask);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + createdEpic.getId() + "/subtasks"))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Set<Subtask> parsedSubtasks = gson.fromJson(response.body(), new SubtasksTypeToken().getType());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(creaetedSubtask.getName(), parsedSubtasks.stream().findFirst().get().getName(), "Некорректное имя подзадачи эпкика");
    }

    @Test
    void getSubtasksByEpicNotFound() throws IOException, InterruptedException {
        int id = 1;
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + id + "/subtasks"))
                .header("Accept", "application/json;charset=utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Задача с id = " + id + " не найдена", error.message, "Сообщение об ошибке не совпадает");
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic delete", "Epic testing", TaskStatus.NEW);
        final Epic createdEpic = manager.createEpic(epic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + createdEpic.getId()))
                .header("Accept", "application/json;charset=utf-8")
                .DELETE()
                .build();

        assertEquals(1, manager.getAllEpics().size(), "Некорректное количество эпиков перед удалением");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals(0, manager.getAllEpics().size(), "Некорректное количество эпиков после удаления");
    }

    @Test
    void getEpicByIdNotFound() throws IOException, InterruptedException {
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
    void addEpicWithEmptyBody() throws IOException, InterruptedException {
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
    void addEpicWithIncorrectBody() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"Incorrect epic\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Код ответа не совпадает");

        ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);

        assertEquals("Неверное тело запроса", error.message, "Сообщение об ошибке не совпадает");
    }

    @Test
    void deleteEpicNotFound() throws IOException, InterruptedException {
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