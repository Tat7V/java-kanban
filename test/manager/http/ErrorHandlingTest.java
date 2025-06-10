package manager.http;

import manager.model.Status;
import manager.model.Task;
import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlingTest extends HttpTests {
    @Test
    public void testGetNonExistentTask() throws Exception {
        HttpResponse<String> response = sendRequest("/tasks/123", "GET", null);

        assertEquals(404, response.statusCode(), "Статус ответа не 404 (Not Found)");
        assertEquals("Not Found", response.body(), "Некорректное тело ответа");
    }

    @Test
    public void createTaskWithTimeOverlap_Returns406() throws Exception {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        manager.createTask(task1);
        Task task2 = new Task(0,"Задача 2", "Описание задачи 2", Status.NEW,
                task1.getStartTime().plusMinutes(20),  Duration.ofMinutes(30));

        HttpResponse<String> response = sendRequest("/tasks", "POST", gson.toJson(task2));

        assertEquals(406, response.statusCode(), "Статус ответа не 406 (Not Acceptable)");
        assertEquals("Not Acceptable", response.body(), "Статус ответа не 406 (Not Acceptable)");
    }
}
