package manager.service;

import manager.model.Status;
import manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;
    private Task task2;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task(1, "Задача", "Описание", Status.NEW,null,null);
        task2 = new Task(2, "Задача 2", "Описание задачи 2", Status.NEW,null,null);
    }

    @Test
    void shouldAddTasksToHistory() {
        historyManager.add(task);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldRemoveDuplicatesFromHistory() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
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


}