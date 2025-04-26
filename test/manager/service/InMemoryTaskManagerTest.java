package manager.service;

import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }
    @Test
    void shouldAddAndFindTasksTypes() {
        Task task = new Task(1,"Задача", "Описание задачи", Status.NEW);
        manager.createTask(task);

        Epic epic = new Epic(2,"Эпик", "Описание эпика", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(3,"Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        ArrayList<Task> tasks = manager.getAllTasks();
        ArrayList<Epic> epics = manager.getAllEpics();
        ArrayList<Subtask> subtasks = manager.getAllSubtasks();

        assertFalse(tasks.isEmpty(), "Список задач не может быть пустым");
        assertFalse(epics.isEmpty(), "Список эпиков не может быть пустым");
        assertFalse(subtasks.isEmpty(), "Список подзадач не может быть пустым");

        Task foundTask = tasks.get(0);
        assertEquals("Задача", foundTask.getName(), "Имя задач должно совпадать");

        Epic foundEpic = epics.get(0);
        assertEquals("Эпик", foundEpic.getName(), "Имя эпика должно совпадать");

        Subtask foundSubtask = subtasks.get(0);
        assertEquals("Подзадача", foundSubtask.getName(), "Имя подзадачи должно совпадать");
    }

    @Test
    void shouldPreserveFieldsOfAddedTask() {
        String expectedName = "Название задачи";
        String expectedDescription = "Описание задачи";
        Status expectedStatus = Status.NEW;

        Task original = new Task(1, expectedName, expectedDescription, expectedStatus);
        manager.createTask(original);

        ArrayList<Task> tasks = manager.getAllTasks();
        assertFalse(tasks.isEmpty(), "Список задач не может быть пустым");

        Task savedTask = tasks.get(0);

        assertEquals(expectedName, savedTask.getName(), "Имя задачи должно совпадать с именем при создании");
        assertEquals(expectedDescription, savedTask.getDescription(), "Описание задачи должно совпадать с описанием при создании");
        assertEquals(expectedStatus, savedTask.getStatus(), "Статус задачи должен совпадать со статусом при создании");
    }
    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = new Task(1, "Название задачи", "Описание задачи", Status.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId());

        manager.deleteTaskById(task.getId());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveEpicAndSubtasksFromHistory() {
        Epic epic = new Epic(1, "Название эпика", "Описание эпика", Status.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Название подзадачи", "Описание подзадачи", Status.NEW, 1);
        manager.createSubtask(subtask);

        manager.getEpicById(1);
        manager.getSubtaskById(2);

        manager.deleteEpicById(1);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldCleanUpDeletedSubtaskIds() {
        Epic epic = new Epic(1, "Эпик", "Описание", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание", Status.NEW, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(2, epic.getSubtaskIds().size());
        assertTrue(epic.getSubtaskIds().contains(subtask1.getId()));
        assertTrue(epic.getSubtaskIds().contains(subtask2.getId()));

        manager.deleteSubtaskById(subtask1.getId());

        assertEquals(1, epic.getSubtaskIds().size());
        assertFalse(epic.getSubtaskIds().contains(subtask1.getId()));
        assertTrue(epic.getSubtaskIds().contains(subtask2.getId()));

        manager.deleteSubtaskById(subtask2.getId());

        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    void shouldIgnoreIdChangesViaSetter() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW);
        manager.createTask(task);

        task.setId(2);

        assertEquals(1, task.getId(), "id задачи не должен изменяться вручную");

        assertNotNull(manager.getTaskById(1), "Задача должна быть доступна по исходному id");
        assertNull(manager.getTaskById(2), "Задача не должна быть доступна по установленному id");

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

}