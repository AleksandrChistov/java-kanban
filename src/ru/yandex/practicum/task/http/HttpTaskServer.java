package ru.yandex.practicum.task.http;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.task.http.handlers.*;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.managers.FileBackedTaskManager;
import ru.yandex.practicum.task.managers.Managers;

import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final String API_ADDRESS = "localhost";
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(API_ADDRESS, PORT), 0);

            httpServer.createContext("/tasks", new TasksHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
            httpServer.createContext("/epics", new EpicsHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(fileBackedTaskManager);
        taskServer.start();
    }
}
