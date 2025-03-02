package ru.yandex.practicum.task.server.handles;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.task.error.NotFoundException;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.server.enums.Endpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final String endpointName;
    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager, String endpointName) {
        this.taskManager = taskManager;
        this.endpointName = endpointName;
    }

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("Началась обработка /" + endpointName + " запроса от клиента.");

        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET -> handleGetItems(exchange);
            case GET_BY_ID -> handleGetItemById(exchange);
            case POST -> handlePostItem(exchange);
            case DELETE -> handleDeleteItem(exchange);
            case GET_CHILDREN_BY_ID -> handleGetChildrenItemsById(exchange);
            default -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    abstract protected void handleGetItems(HttpExchange exchange);

    protected void handleGetItemById(HttpExchange exchange) {
        writeResponse(exchange, "Такой эндпоинт не реализован", 501);
    }

    protected void handlePostItem(HttpExchange exchange) {
        writeResponse(exchange, "Такой эндпоинт не реализован", 501);
    }

    protected void handleDeleteItem(HttpExchange exchange) {
        writeResponse(exchange, "Такой эндпоинт не реализован", 501);
    }

    protected void handleGetChildrenItemsById(HttpExchange exchange) {
        writeResponse(exchange, "Такой эндпоинт не реализован", 501);
    }

    protected int getId(HttpExchange exchange) throws NotFoundException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException | NullPointerException exception) {
            throw new NotFoundException(pathParts[2]);
        }
    }

    protected Optional<String> getBody(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            return Optional.of(body);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    protected void sendSuccess(HttpExchange exchange) {
        writeResponse(exchange, "", 200);
    }

    protected void sendText(HttpExchange exchange) {
        writeResponse(exchange, "", 201);
    }

    protected void sendNotFound(HttpExchange exchange, String message) {
        writeResponse(exchange, message, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) {
        writeResponse(exchange, message, 406);
    }

    protected void writeResponse(HttpExchange exchange, String responseString, int responseCode) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            writeResponse(exchange, "Внутренняя ошибка сервера", 500);
        }
        exchange.close();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals(endpointName)) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST;
            }
        }

        if (pathParts.length == 3 && pathParts[1].equals(endpointName)) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_BY_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE;
            }
        }

        if (pathParts.length == 4 && pathParts[1].equals(endpointName) && pathParts[3].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_CHILDREN_BY_ID;
            }
        }

        return Endpoint.UNKNOWN;
    }
}
