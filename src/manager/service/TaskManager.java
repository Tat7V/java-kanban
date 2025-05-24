package manager.service;

import manager.model.Epic;
import manager.model.Subtask;
import manager.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {

    List<Task> getPrioritizedTasks();

    //d. Создание.
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    //a. Получение списка всех задач.
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    //b. Удаление всех задач.
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    // c. Получение по идентификатору.
    Optional<Task> getTaskById(int id);

    Optional<Epic> getEpicById(int id);

    Optional<Subtask> getSubtaskById(int id);

    //e. Обновление.
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void updateEpicStatus(int id);

    //f. Удаление по идентификатору.
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    //a.a Получение списка всех подзадач определённого эпика.
    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

}
