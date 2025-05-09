package manager.service;

import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import manager.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        manager.save();

        String content = Files.readString(tempFile.toPath());
        assertEquals("id,type,name,status,description,epic", content.trim());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());

    }

    @Test
    void shouldSaveSeveralTasks() throws IOException {

        Task task = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.createTask(task);


        Epic epic = new Epic(2, "Эпик 1", "Описание эпика 1", Status.IN_PROGRESS);
        manager.createEpic(epic);


        Subtask subtask = new Subtask(3, "Подзадача 1", "Описание подзадачи 1", Status.IN_PROGRESS, 2);
        manager.createSubtask(subtask);


        List<String> lines = Files.readAllLines(tempFile.toPath());

        assertEquals(4, lines.size());
        assertEquals("1,TASK,Задача 1,NEW,Описание задачи 1,", lines.get(1));
        assertEquals("2,EPIC,Эпик 1,IN_PROGRESS,Описание эпика 1,", lines.get(2));
        assertEquals("3,SUBTASK,Подзадача 1,IN_PROGRESS,Описание подзадачи 1,2", lines.get(3));

    }

    @Test
    void shouldLoadSeveralTasks() {

        Epic epic = new Epic(1, "Epic", "Epic desc", Status.NEW);
        manager.createEpic(epic);

        Task task = new Task(2, "Task", "Task desc", Status.IN_PROGRESS);
        manager.createTask(task);

        Subtask subtask = new Subtask(3, "Subtask", "Sub desc", Status.DONE, 1);
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        Task loadedTask = loadedManager.getTaskById(2);
        assertEquals("Task", loadedTask.getName());
        assertEquals(Status.IN_PROGRESS, loadedTask.getStatus());

        assertEquals(1, loadedManager.getAllEpics().size());
        Epic loadedEpic = loadedManager.getEpicById(1);
        assertEquals("Epic", loadedEpic.getName());
        assertEquals(Status.DONE, loadedEpic.getStatus());

        assertEquals(1, loadedManager.getAllSubtasks().size());
        Subtask loadedSubtask = loadedManager.getSubtaskById(3);
        assertEquals("Subtask", loadedSubtask.getName());
        assertEquals(1, loadedSubtask.getEpicId());

    }
}


