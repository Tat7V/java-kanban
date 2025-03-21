package manager;

import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import manager.model.Task;
import manager.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(0, "Task 1", "Покупки", Status.NEW);
        Task task2 = new Task(0, "Task 2", "Уборка", Status.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);


        Epic epic1 = new Epic(0, "Epic 1", "Большая задача 1", Status.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Subtask 1", "Подзадача 1 большой задачи 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Subtask 2", "Подзадача 2 большой задачи 1", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);


        Epic epic2 = new Epic(0, "Epic 2", "Большая задача 2", Status.NEW);
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask(0, "Subtask 3", "Подзадача 1 большой задачи 2", Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("Subtasks: " + taskManager.getAllSubtasks());

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        System.out.println("Updated Tasks: " + taskManager.getAllTasks());
        System.out.println("Updated Epics: " + taskManager.getAllEpics());
        System.out.println("Updated Subtasks: " + taskManager.getAllSubtasks());

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());

        System.out.println("Final Tasks: " + taskManager.getAllTasks());
        System.out.println("Final Epics: " + taskManager.getAllEpics());
        System.out.println("Final Subtasks: " + taskManager.getAllSubtasks());















    }
}
