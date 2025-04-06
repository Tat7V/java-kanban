package manager.model;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicShouldNotAddSelfAsSubtask() {
        Epic epic = new Epic(1, "Эпик", "Описание", Status.NEW);
        epic.addSubtaskId(epic.getId());
        assertEquals(0, epic.getSubtaskIds().size(), "Эпик нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic(1, "Эпик 1", "Описание 1", Status.NEW);
        Epic epic2 = new Epic(1, "Эпик 2", "Описание 2", Status.DONE);
        assertEquals(epic1, epic2, "Эпики с одинаковыми id должны быть равны");

    }
}