package manager;

import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import manager.model.Task;
import manager.service.Managers;
import manager.service.TaskManager;

public class Main {
    //сценарий для проверки
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task = new Task(0,"Покупки", "Купить продукты", Status.NEW);
        manager.createTask(task);

        Epic epic = new Epic(0,"Ремонт", "Сделать ремнот в квартире", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(0,"Поклеить обои", "Зал и спальня", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(task.getId());
        manager.getEpicById(epic.getId());

        printAllTasks(manager);

    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
