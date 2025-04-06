package manager.service;

import manager.model.Status;
import manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task(1, "Задача", "Описание", Status.NEW);
    }

    @Test
    void shouldPreserveTaskDataInHistory() {
        historyManager.add(task);
        Task saved = historyManager.getHistory().get(0);

        assertEquals(task.getId(), saved.getId());
        assertEquals(task.getName(), saved.getName());
        assertEquals(task.getDescription(), saved.getDescription());
        assertEquals(task.getStatus(), saved.getStatus());
    }

    @Test
    void shouldNotExceedMaxHistorySize() {
        for (int i = 1; i <= 12; i++) {
            historyManager.add(new Task(i, "Задача " + i, "Описание", Status.NEW));
        }
        assertEquals(10, historyManager.getHistory().size(), "В истории может храниться не больше 10 задач");
    }


}