package manager.service;

import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import manager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;
    private final LocalDateTime testTime = LocalDateTime.of(2025, 5, 20, 12, 10);


    @BeforeEach
    void setUp() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось найти временный файл", e);
        }
        manager = createManager();
    }

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        manager.save();

        String content = Files.readString(tempFile.toPath());
        assertEquals("id,type,name,status,description,epic,startTime,duration,endTime", content.trim());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveSeveralTasks() throws IOException {

        Task task = new Task(1, "Задача", "Описание задачи 1", Status.NEW, null, null);
        manager.createTask(task);

        Epic epic = new Epic(2, "Эпик", "Описание эпика 1", Status.IN_PROGRESS);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(3, "Подзадача", "Описание подзадачи 1", Status.IN_PROGRESS, 2, null, null);
        manager.createSubtask(subtask);

        List<String> lines = Files.readAllLines(tempFile.toPath());

        assertEquals(4, lines.size());
        assertEquals("1,TASK,Задача,NEW,Описание задачи 1,,,", lines.get(1));
        assertEquals("2,EPIC,Эпик,IN_PROGRESS,Описание эпика 1,,,", lines.get(2));
        assertEquals("3,SUBTASK,Подзадача,IN_PROGRESS,Описание подзадачи 1,2,,", lines.get(3));

    }

    @Test
    void shouldLoadSeveralTasks() {

        Epic epic = new Epic(1, "Эпик", "Описание эпика", Status.NEW);
        manager.createEpic(epic);

        Task task = new Task(2, "Задача", "Описание задачи", Status.IN_PROGRESS, null, null);
        manager.createTask(task);

        Subtask subtask = new Subtask(3, "Подзадача", "Описание подзадачи", Status.DONE, 1, null, null);
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        Task loadedTask = loadedManager.getTaskById(2)
                .orElseThrow(() -> new AssertionError("Задача не найдена"));

        assertEquals("Задача", loadedTask.getName());
        assertEquals(Status.IN_PROGRESS, loadedTask.getStatus());

        assertEquals(1, loadedManager.getAllEpics().size());
        Epic loadedEpic = loadedManager.getEpicById(1)
                .orElseThrow(() -> new AssertionError("Эпик не найден"));

        assertEquals("Эпик", loadedEpic.getName());
        assertEquals(Status.DONE, loadedEpic.getStatus());

        assertEquals(1, loadedManager.getAllSubtasks().size());
        Subtask loadedSubtask = loadedManager.getSubtaskById(3)
                .orElseThrow(() -> new AssertionError("Подзадача не найдена"));

        assertEquals("Подзадача", loadedSubtask.getName());
        assertEquals(1, loadedSubtask.getEpicId());
    }

    @Test
    void shouldSaveAndLoadTasksTime() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW,
                testTime, Duration.ofMinutes(30));
        manager.createTask(task);
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loaded.getTaskById(1)
                .orElseThrow(() -> new AssertionError("Задача не найдена после загрузки"));

        assertEquals(task.getStartTime(), loadedTask.getStartTime(),
                "Время начала должно сохраняться");
        assertEquals(task.getDuration(), loadedTask.getDuration(),
                "Длительность должна сохраняться");
    }
}


