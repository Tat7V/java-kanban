package manager.http;

import manager.model.Status;
import manager.model.Task;
import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryAndPriorityTest extends HttpTests {
    @Test
    public void testGetHistory() throws Exception {
        Task task = new Task(1,"Задача 1", "Описание задачи 1", Status.NEW, null, null);
        manager.createTask(task);
        manager.getTaskById(task.getId());

        HttpResponse<String> response = sendRequest("/history", "GET", null);

        assertEquals(200, response.statusCode(),"Статус ответа не 200 (OK)");
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, history.length, "История пуста");
    }

    @Test
    public void getPrioritizedTasks() throws Exception {
        Task task1 = new Task(1,"Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        Task task2 = new Task(2,"Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(30));
        manager.createTask(task1);
        manager.createTask(task2);

        HttpResponse<String> response = sendRequest("/prioritized", "GET", null);

        assertEquals(200, response.statusCode(), "Статус ответа не 200 (OK)");
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertTrue(tasks[0].getStartTime().isBefore(tasks[1].getStartTime()));
    }
}
