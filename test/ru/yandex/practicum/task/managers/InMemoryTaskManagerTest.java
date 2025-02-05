package ru.yandex.practicum.task.managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends AbstractTaskManagerTest {
    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}