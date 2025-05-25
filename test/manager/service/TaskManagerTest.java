package manager.service;

import manager.exceptions.ManagerSaveException;
import manager.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected LocalDateTime testTime = LocalDateTime.of(2025, 5, 20, 12, 10);
    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    // Тесты для Task
    @Test
    void shouldCreateAndGetTask() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW, testTime, Duration.ofMinutes(30));
        manager.createTask(task);
        Optional<Task> savedTask = manager.getTaskById(1);
        assertTrue(savedTask.isPresent(), "Задача должна существовать");
        assertEquals(task.getName(), savedTask.get().getName(), "Названия задач должны совпадать");
        assertEquals(testTime, savedTask.get().getStartTime(), "У задачи должно быть время ее начала");
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW, null, null);
        manager.createTask(task);

        Task updated = new Task(1, "Обновление задачи", "Новое описание", Status.IN_PROGRESS,
                testTime, Duration.ofHours(1));
        manager.updateTask(updated);

        Task saved = manager.getTaskById(1).orElseThrow();
        assertEquals("Обновление задачи", saved.getName(), "Название должно обновиться");
        assertEquals(Status.IN_PROGRESS, saved.getStatus(), "Статус должен обновиться");
        assertEquals(testTime, saved.getStartTime(), "Время должно обновиться");
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW, null, null);
        manager.createTask(task);
        manager.deleteTaskById(1);

        assertTrue(manager.getTaskById(1).isEmpty(), "Задача должна удалиться");
        assertTrue(manager.getHistory().isEmpty(), "Задача должна удаляться из истории");
    }

    @Test
    void shouldIgnoreIdChangesViaSetter() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW,null,null);
        manager.createTask(task);

        task.setId(2);

        assertEquals(1, task.getId(), "id задачи не должен изменяться вручную");

        assertTrue(manager.getTaskById(1).isPresent(),"Задача должна быть доступна по исходному id");
        assertTrue(manager.getTaskById(2).isEmpty(),"Задача не должна быть доступна по установленному id");

        manager.getTaskById(1);
        List<Task> history = manager.getHistory();
        boolean found = false;
        for (Task t : history) {
            if (t.getId() == 1) {
                found = true;
                break;
            }
        }
        assertTrue(found, "История должна содержать задачу с исходным id");
    }

    // Тесты для Epic
    @Test
    void shouldCalculateEpicStatus() {
        Epic epic = new Epic(1, "Эпик", "Описание", Status.NEW);
        manager.createEpic(epic);

        // Все подзадачи NEW → эпик NEW
        Subtask sub1 = new Subtask(2, "Подзадача", "Описание подзадачи", Status.NEW, 1, null, null);
        manager.createSubtask(sub1);
        assertEquals(Status.NEW, manager.getEpicById(1).get().getStatus());

        // Все подзадачи DONE → эпик DONE
        sub1.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        assertEquals(Status.DONE, manager.getEpicById(1).get().getStatus());

        // NEW и DONE → IN_PROGRESS
        Subtask sub2 = new Subtask(3, "Подзадача2", "Описание подзадачи2", Status.NEW, 1, null, null);
        manager.createSubtask(sub2);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(1).get().getStatus());

        // Подзадачи IN_PROGRESS → эпик IN_PROGRESS
        sub2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(sub2);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(1).get().getStatus());
    }

    @Test
    void shouldCalculateEpicTime() {
        Epic epic = new Epic(1, "Эпик", "Описание", Status.NEW);
        manager.createEpic(epic);
        Subtask sub1 = new Subtask(2, "Подзадача1", "Описание подзадачи1", Status.NEW, 1,
                testTime, Duration.ofHours(2));
        Subtask sub2 = new Subtask(3, "Подзадача2", "Описание подзадачи2", Status.NEW, 1,
                testTime.plusHours(3), Duration.ofHours(1));
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        Epic savedEpic = manager.getEpicById(1).orElseThrow();
        assertEquals(testTime, savedEpic.getStartTime(), "Время начала эпика");
        assertEquals(Duration.ofHours(3), savedEpic.getDuration(), "Длительность эпика");
        assertEquals(testTime.plusHours(4), savedEpic.getEndTime(), "Время окончания эпика");
    }

    // Тесты для Subtask
    @Test
    void shouldLinkSubtaskToEpic() {
        Epic epic = new Epic(1, "Эпик", "Описание", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Подзадача", "Описание подзадчи", Status.NEW, 1, null, null);
        manager.createSubtask(subtask);

        assertTrue(manager.getEpicById(1).get().getSubtaskIds().contains(2),
                "Эпик должен содержать подзадачу");
        assertEquals(1, manager.getSubtaskById(2).get().getEpicId(),
                "Подзадача должна ссылаться на эпик");
    }

    @Test
    void shouldRemoveSubtaskFromEpic() {
        Epic epic = new Epic(1, "Эпик", "Описание", Status.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Подзадача", "Описание", Status.NEW, 1, null, null);
        manager.createSubtask(subtask);

        manager.deleteSubtaskById(2);
        assertFalse(manager.getEpicById(1).get().getSubtaskIds().contains(2),
                "Подзадача должна удаляться из эпика");
    }

    //Тесты для истории
    @Test
    void shouldAddViewedTasksToHistory() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW, null, null);
        manager.createTask(task);
        manager.getTaskById(1);

        assertEquals(1, manager.getHistory().size(), "История должна содержать просмотренную задачу");
        assertEquals(task, manager.getHistory().get(0), "Задача в истории должна совпадать");
    }

    @Test
    void shouldNotDuplicateHistoryEntries() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW, null, null);
        manager.createTask(task);
        manager.getTaskById(1);
        manager.getTaskById(1);

        assertEquals(1, manager.getHistory().size(), "История не должна дублировать задачи");
    }

    // Тесты для временных интервалов
    @Test
    void shouldDetectTimeOverlaps() {
        Task task1 = new Task(1, "Задача1", "Описание задачи1", Status.NEW,
                testTime, Duration.ofHours(2));
        manager.createTask(task1);

        Task task2 = new Task(2, "Задача2", "Описание задачи2", Status.NEW,
                testTime.plusHours(1), Duration.ofHours(1));

        assertThrows(ManagerSaveException.class,
                () -> manager.createTask(task2),
                "Задача пересекается по времени с имеющейся");
    }

    @Test
    void shouldAddSequentialTasks() {
        Task task1 = new Task(1, "Задача1", "Описание задачи1", Status.NEW,
                testTime, Duration.ofHours(1));
        manager.createTask(task1);

        Task task2 = new Task(2, "Задача2", "Описание задачи2", Status.NEW,
                testTime.plusHours(2), Duration.ofHours(1));

        assertDoesNotThrow(() -> manager.createTask(task2),
                "Задачи без пересечения по времени должны создаваться");
    }

    /* Тесты для приоритетного списка */
    @Test
    void shouldReturnTasksInTimeOrder() {
        Task earlyTask = new Task(1, "Задача, добавленная раньше", "Описание1", Status.NEW,
                testTime, Duration.ofHours(1));
        Task lateTask = new Task(2, "Задача, добавленная позже", "Описание2", Status.NEW,
                testTime.plusHours(2), Duration.ofHours(1));
        manager.createTask(lateTask);
        manager.createTask(earlyTask);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(1, prioritized.get(0).getId(), "Ранняя задача должна быть первой");
        assertEquals(2, prioritized.get(1).getId(), "Поздняя задача должна быть второй");
    }

    @Test
    void shouldExcludeTasksWithoutTime() {
        Task withTime = new Task(1, "Timed", "Desc", Status.NEW,
                testTime, Duration.ofHours(1));
        Task withoutTime = new Task(2, "NoTime", "Desc", Status.NEW, null, null);
        manager.createTask(withTime);
        manager.createTask(withoutTime);

        assertEquals(1, manager.getPrioritizedTasks().size(), "Только задачи со временем");
        assertEquals(1, manager.getPrioritizedTasks().get(0).getId(), "Только задача со временем");
    }
}