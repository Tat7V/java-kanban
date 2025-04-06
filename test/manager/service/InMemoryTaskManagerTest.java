package manager.service;

import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


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

}