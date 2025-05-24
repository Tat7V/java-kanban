package manager.service;

import manager.exceptions.ManagerSaveException;
import manager.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

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
            sb.append("id,type,name,status,description,epic,startTime,duration,endTime\n");

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
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    private String taskToString(Task task) {
        TaskType type = task.getType();
        String epicId = "";
        String startTime = "";
        String duration = "";


        if (task.getStartTime() != null) {
            startTime = task.getStartTime().toString();
        }

        if (task.getDuration() != null) {
            duration = String.valueOf(task.getDuration().toMinutes());
        }

        if (type == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId,
                startTime,
                duration
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isEmpty()) continue;

                Task task = taskFromString(lines[i]);
                switch (task.getType()) {
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(subtask.getId(), subtask);

                        Epic epic = manager.epics.get(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }
                        break;
                    default:
                        manager.tasks.put(task.getId(), task);
                }

                if (task.getStartTime() != null) {
                    manager.getPrioritizedTasks().add(task);
                }
            }

            for (Epic epic : manager.epics.values()) {
                manager.updateEpicTime(epic);
                manager.updateEpicStatus(epic.getId());
            }

            int maxId = getMaxId(manager);
            manager.idNext = maxId + 1;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        return manager;
    }

    private static int getMaxId(FileBackedTaskManager manager) {
        return Stream.of(
                        manager.getAllTasks().stream(),
                        manager.getAllEpics().stream(),
                        manager.getAllSubtasks().stream()
                )
                .flatMap(stream -> stream)
                .mapToInt(Task::getId)
                .max()
                .orElse(0);
    }


    private static Task taskFromString(String value) {
        String[] taskFields = value.split(",");
        int id = Integer.parseInt(taskFields[0]);
        TaskType type = TaskType.valueOf(taskFields[1]); // Получаем тип из enum
        String name = taskFields[2];
        Status status = Status.valueOf(taskFields[3]);
        String description = taskFields[4];

        LocalDateTime startTime = null;
        if (taskFields.length > 6 && !taskFields[6].isEmpty()) {
            startTime = LocalDateTime.parse(taskFields[6]);
        }

        Duration duration = null;
        if (taskFields.length > 7 && !taskFields[7].isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(taskFields[7]));
        }

        if (type == TaskType.TASK) {
            return new Task(id, name, description, status, startTime, duration);
        } else if (type == TaskType.EPIC) {
            Epic epic = new Epic(id, name, description, status);
            if (taskFields.length > 8 && !taskFields[8].isEmpty()) {
                epic.setEndTime(LocalDateTime.parse(taskFields[8]));
            }
            return epic;
        } else if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(taskFields[5]);
            return new Subtask(id, name, description, status, epicId, startTime, duration);
        }
        throw new IllegalArgumentException("Неизвестный тип задачи");
    }


}