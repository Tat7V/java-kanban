package manager.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Задача 1", "Описание 1", Status.NEW,null,null);
        Task task2 = new Task(1, "Задача 2", "Описание 2", Status.DONE,null,null);

        assertEquals(task1, task2, "Задачи с одинаковым id равны по условию");
    }


}