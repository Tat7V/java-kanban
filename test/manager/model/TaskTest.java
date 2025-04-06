package manager.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task(1, "Задача 2", "Описание 2", Status.DONE);

        assertEquals(task1, task2, "Задачи с одинаковым id равны по условию");
    }




}