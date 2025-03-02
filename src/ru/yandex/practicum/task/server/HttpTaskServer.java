package ru.yandex.practicum.task.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.managers.Managers;
import ru.yandex.practicum.task.server.handles.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        start(Managers.getFileBacked(new File("/tasks.txt")));
    }

    public static void start(TaskManager manager) {
        TaskManager taskManager = Objects.requireNonNull(manager, "Для запуска сервера необходим объект класса TaskManager");

        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

            httpServer.createContext("/tasks", new TasksHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
            httpServer.createContext("/epics", new EpicsHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

            httpServer.start();

            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
