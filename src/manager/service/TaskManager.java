package manager.service;

import manager.model.Epic;
import manager.model.Subtask;
import manager.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //d. Создание.
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    //a. Получение списка всех задач.
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    //b. Удаление всех задач.
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    // c. Получение по идентификатору.
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    //e. Обновление.
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    //f. Удаление по идентификатору.
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    //a.a Получение списка всех подзадач определённого эпика.
    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

}
