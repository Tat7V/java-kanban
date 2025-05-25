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
            for (int tackNumber = 0; tackNumber < 1000; tackNumber++) {
                manager.createTask(new Task(tackNumber, "Задача" + tackNumber, "Описание задачи" + tackNumber, Status.NEW, null, null));
            }
        }, "Должен обрабатывать большое количество задач");
    }
}