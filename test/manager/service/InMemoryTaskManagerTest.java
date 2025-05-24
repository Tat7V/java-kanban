package manager.service;


import manager.model.Status;
import manager.model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    // Тест на обработку пустой истории
    @Test
    void shouldHandleEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty(),
                "История должна быть пустой при инициализации");
    }

    // Тест на работу с большим количеством задач
    @Test
    void shouldHandleManyTasks() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 1000; i++) {
                manager.createTask(new Task(i, "Задача" + i, "Описание задачи" + i, Status.NEW, null, null));
            }
        }, "Должен обрабатывать большое количество задач");
    }
}