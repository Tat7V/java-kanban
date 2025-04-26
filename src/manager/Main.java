package manager;

import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import manager.model.Task;
import manager.service.Managers;
import manager.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = getTaskManager();

        manager.getTaskById(1);
        printHistory(manager);
        manager.getEpicById(3);
        printHistory(manager);
        manager.getSubtaskById(4);
        printHistory(manager);
        manager.getTaskById(2);
        printHistory(manager);
        manager.getTaskById(2);
        printHistory(manager);
        manager.getSubtaskById(5);
        printHistory(manager);
        manager.getTaskById(1);
        printHistory(manager);

        manager.deleteTaskById(1);
        printHistory(manager);

        manager.deleteEpicById(3);
        printHistory(manager);
    }

    private static TaskManager getTaskManager() {
        TaskManager manager = Managers.getDefault();

        // опциональный пользовательский сценарий

        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);


        Epic epic1 = new Epic(3, "Эпик 1 с подзадачами", "Описание эпика 1", Status.NEW);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask(4, "Подзадача 1", "Описание подзадачи 1", Status.NEW, 3);
        Subtask subtask2 = new Subtask(5, "Подзадача 2", "Описание подзадачи 2", Status.NEW, 3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic(6, "Эпик 2 без подзадач", "Описание эпика 2", Status.NEW);
        manager.createEpic(epic2);
        return manager;
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("History:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}