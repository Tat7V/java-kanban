package manager.service;

import manager.exceptions.ManagerSaveException;
import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import manager.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    final HashMap<Integer, Task> tasks = new HashMap<>();
    final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    final HashMap<Integer, Epic> epics = new HashMap<>();
    int idNext = 1;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null ||
                task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return !task1.getEndTime().isBefore(task2.getStartTime()) &&
                !task1.getStartTime().isAfter(task2.getEndTime());
    }

    private boolean isTimeOverlapping(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .filter(task -> !task.equals(newTask))
                .anyMatch(task -> hasTimeOverlap(newTask, task));
    }

    public void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpicId(epic.getId())
                .stream()
                .filter(subtask -> subtask.getStartTime() != null && subtask.getDuration() != null)
                .collect(Collectors.toList());

        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return;
        }

        // Первый проход: startTime и endTime
        LocalDateTime start = subtasks.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = subtasks.stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // Второй проход: duration
        Duration duration = subtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(duration);
    }

    //d. Создание.
    @Override
    public void createTask(Task task) {
        if (isTimeOverlapping(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей");
        }
        task.setId(idNext++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idNext++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (isTimeOverlapping(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей");
        }
        subtask.setId(idNext++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
            updateEpicTime(epic);
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    //a. Получение списка всех задач.
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    //b. Удаление всех задач.
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        });
    }

    // c. Получение по идентификатору.
    @Override
    public Optional<Task> getTaskById(int id) {
        return Optional.ofNullable(tasks.get(id))
                .map(task -> {
                    historyManager.add(task);
                    return task;
                });
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        return Optional.ofNullable(epics.get(id))
                .map(epic -> {
                    historyManager.add(epic);
                    return epic;
                });
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        return Optional.ofNullable(subtasks.get(id))
                .map(subtask -> {
                    historyManager.add(subtask);
                    return subtask;
                });
    }

    //e. Обновление.
    @Override
    public void updateTask(Task task) {
        Task existingTask = tasks.get(task.getId());
        if (existingTask != null) {
            prioritizedTasks.remove(existingTask);
            if (isTimeOverlapping(task)) {
                prioritizedTasks.add(existingTask);
                throw new ManagerSaveException("Задача пересекается по времени с существующей");
            }
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }


    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (existingSubtask != null) {
            prioritizedTasks.remove(existingSubtask);
            if (isTimeOverlapping(subtask)) {
                prioritizedTasks.add(existingSubtask);
                throw new ManagerSaveException("Подзадача пересекается по времени с существующей");
            }
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic.getId());
                updateEpicTime(epic);
            }
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    //f. Удаление по идентификатору.
    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTime(epic);
            }
            historyManager.remove(id);
        }
    }

    //a.a Получение списка всех подзадач определённого эпика.
    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return Optional.ofNullable(epics.get(epicId))
                .map(Epic::getSubtaskIds)
                .orElse(Collections.emptyList())
                .stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    //Статус эпика
    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = getSubtasksByEpicId(epicId);

        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = epicSubtasks.stream()
                .map(Subtask::getStatus)
                .allMatch(status -> status == Status.DONE);

        boolean allNew = epicSubtasks.stream()
                .map(Subtask::getStatus)
                .allMatch(status -> status == Status.NEW);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}