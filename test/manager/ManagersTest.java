package manager;

import manager.model.Status;
import manager.model.Task;
import manager.service.HistoryManager;
import manager.service.Managers;
import manager.service.TaskManager;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void shouldNotConflictWithGivenId() {
        TaskManager manager = Managers.getDefault();

        Task taskWithPredefinedId = new Task(1, "Задача с установленным id", "Описание", Status.NEW,null,null);
        taskWithPredefinedId.setId(1);
        manager.createTask(taskWithPredefinedId);

        Task nextTask = new Task(2, "Следующая задача со сгенерированным id на 1 больше прошлой", "Описание", Status.NEW,null,null);
        manager.createTask(nextTask);

        List<Task> allTasks = manager.getAllTasks();
        assertEquals(2, allTasks.size(), "Должно быть 2 задачи");

        boolean foundtaskWithPredefinedId = false;
        boolean foundnextTask = false;

        for (Task task : allTasks) {
            if (task.getId() == 1) {
                foundtaskWithPredefinedId = true;
            }
            if (task.getId() == 2) {
                foundnextTask = true;
            }
        }
        assertTrue(foundtaskWithPredefinedId, "Должна существовать задача с id=1");
        assertTrue(foundnextTask, "Должна существовать задача с id=2");

        assertNotEquals(
                allTasks.get(0).getId(),
                allTasks.get(1).getId(),
                "id задач должны различаться"
        );
    }

    @Test
    void shouldReturnInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }
}