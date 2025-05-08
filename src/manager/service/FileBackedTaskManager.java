package manager.service;

import manager.exceptions.ManagerSaveException;
import manager.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();

    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    void save() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                sb.append(taskToString(task)).append("\n");
            }
            for (Epic epic : getAllEpics()) {
                sb.append(taskToString(epic)).append("\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                sb.append(taskToString(subtask)).append("\n");
            }

            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String taskToString(Task task) {
        TaskType type = task.getType();
        String epicId = "";

        if (type == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId);
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                Task task = taskFromString(lines[i]);
                if (task.getType() == TaskType.EPIC) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }

            int maxId = getMaxId(manager);
            manager.idNext = maxId + 1;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        return manager;
    }

    private static int getMaxId(FileBackedTaskManager manager) {
        int maxId = 0;
        for (Task task : manager.getAllTasks()) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        for (Epic epic : manager.getAllEpics()) {
            if (epic.getId() > maxId) {
                maxId = epic.getId();
            }
        }
        for (Subtask subtask : manager.getAllSubtasks()) {
            if (subtask.getId() > maxId) {
                maxId = subtask.getId();
            }
        }
        return maxId;
    }


    private static Task taskFromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]); // Получаем тип из enum
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        if (type == TaskType.TASK) {
            return new Task(id, name, description, status);
        } else if (type == TaskType.EPIC) {
            return new Epic(id, name, description, status);
        } else if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(fields[5]);
            return new Subtask(id, name, description, status, epicId);
        }
        throw new IllegalArgumentException("Неизвестный тип задачи");
    }

    // Пользовательский сценарий
    public static void main(String[] args) throws Exception {
        File tempFile = File.createTempFile("tasks", ".csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic(1, "Поездка", "Спланировать поездку", Status.IN_PROGRESS);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Вещи", "Купить к поездке", Status.IN_PROGRESS, 1);
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        System.out.println("Эпики после загрузки: " + loadedManager.getAllEpics());
        System.out.println("Подзадачи после загрузки: " + loadedManager.getAllSubtasks());

        //должен id установить новый он (maxId + 1)
        Epic epic1 = new Epic(0, "Для пляжа", "Что надо с собой", Status.NEW);
        manager.createEpic(epic1);
        System.out.println(epic1);
    }


}


