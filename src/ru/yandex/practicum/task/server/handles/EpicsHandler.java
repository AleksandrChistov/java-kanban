package ru.yandex.practicum.task.server.handles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.task.enums.TaskStatus;
import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.tasks.Epic;
import ru.yandex.practicum.task.tasks.Task;

import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager, "epics");
    }

    @Override
    protected void handleGetItems(HttpExchange exchange) {
        String response = taskManager.getAllEpics().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        writeResponse(exchange, response.isBlank() ? "[]" : response, 200);
    }

    @Override
    protected void handleGetItemById(HttpExchange exchange) {
        try {
            Epic epic = taskManager.getEpic(getId(exchange));
            writeResponse(exchange, epic.toString(), 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    @Override
    protected void handlePostItem(HttpExchange exchange) {
        Optional<String> bodyStr = getBody(exchange);

        if (bodyStr.isPresent()) {
            JsonElement jsonElement = JsonParser.parseString(bodyStr.get());
            try {
                if (!jsonElement.isJsonObject()) {
                    throw new JsonSyntaxException("Неверное тело запроса");
                }

                Epic epic = getEpicFromJson(jsonElement);

                if (epic.getId() == null) {
                    taskManager.createEpic(epic);
                } else {
                    taskManager.updateEpic(epic);
                }

                sendText(exchange);
            } catch (NullPointerException | DateTimeParseException | JsonSyntaxException e) {
                writeResponse(exchange, "Неверное тело запроса", 400);
            } catch (Exception e) {
                writeResponse(exchange, "Внутренняя ошибка сервера", 500);
            }
        } else {
            writeResponse(exchange, "Тело запроса не может быть пустым", 400);
        }
    }

    @Override
    protected void handleGetChildrenItemsById(HttpExchange exchange) {
        try {
            Epic epic = taskManager.getEpic(getId(exchange));
            String response = taskManager.getSubtasksByEpic(epic).stream()
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));
            writeResponse(exchange, response.isBlank() ? "[]" : response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    @Override
    protected void handleDeleteItem(HttpExchange exchange) {
        try {
            int id = getId(exchange);
            Epic epic = taskManager.deleteEpic(id);

            if (epic == null) {
                throw new NotFoundException(String.valueOf(id));
            }

            sendSuccess(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private Epic getEpicFromJson(JsonElement jsonElement) throws NullPointerException, DateTimeParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Integer id = jsonObject.get("id") != null ? jsonObject.get("id").getAsInt() : null;
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());

        Epic newEpic = new Epic(name, description, status);

        if (id != null) {
            newEpic.setId(id);
        }

        return newEpic;
    }
}
