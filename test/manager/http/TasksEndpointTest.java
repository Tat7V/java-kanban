package manager.http;

import manager.model.Status;
import manager.model.Task;
import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TasksEndpointTest extends HttpTests{

    @Test
    public void testCreateTask() throws Exception {
        Task task = new Task(0,"Задача 1","Описание задачи 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(120));
        String json = gson.toJson(task);

        HttpResponse<String> response = sendRequest("/tasks", "POST", json);

        assertEquals(201, response.statusCode(), "Статус ответа не 201 (Created)");

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size(), "Задача не сохранилась в менеджере");
    }

    @Test
    public void testGetTaskById() throws Exception {
         Task task = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        manager.createTask(task);
        int taskId = task.getId();

        HttpResponse<String> response = sendRequest("/tasks/1", "GET", null);

        assertEquals(200, response.statusCode(), "Статус ответа не 200 (OK)");

        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(taskId, receivedTask.getId(), "ID задачи не совпадает");
        assertEquals("Задача 1", receivedTask.getName(), "Имя задачи не совпадает");
    }

    @Test
    public void testDeleteTask() throws Exception {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        manager.createTask(task);

        HttpResponse<String> response = sendRequest("/tasks/1", "DELETE", null);

        assertEquals(200, response.statusCode(), "Статус ответа не 200 (OK)");
        assertTrue(manager.getAllTasks().isEmpty(), "Задача не удалилась из менеджера");
    }
}

