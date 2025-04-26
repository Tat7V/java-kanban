package manager.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {
    @Test
    void subtaskShouldNotBeItsOwnEpic() {
        Subtask subtask = new Subtask(1, "Subtask", "Description", Status.NEW, 2);
        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Подзадачу нельзя сделать своим же эпиком");
    }

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask(1, "Подзадача 1", "Описание 1", Status.NEW, 10);
        Subtask subtask2 = new Subtask(1, "Подзадача 2", "Описание 2", Status.DONE, 20);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковыми id должны быть равны");
    }

}