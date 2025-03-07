package ru.yandex.practicum.task.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.task.http.adapters.DurationTypeAdapter;
import ru.yandex.practicum.task.http.adapters.LocalDateTimeTypeAdapter;
import ru.yandex.practicum.task.interfaces.TaskManager;
import ru.yandex.practicum.task.managers.Managers;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

class HttpTaskManagerTest {
    protected final TaskManager manager = Managers.getInMemoryManager();
    protected final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();
    protected final HttpClient client = HttpClient.newHttpClient();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);

    @BeforeEach
    void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }
}