package manager.http;

import manager.model.Epic;
import manager.model.Status;
import manager.model.Subtask;
import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicsEndpointTest extends HttpTests {
    @Test
    public void testCreateEpic() throws Exception {
        Epic epic = new Epic(0,"Эпик 1", "Описание эпика 1", Status.NEW);
        HttpResponse<String> response = sendRequest("/epics", "POST", gson.toJson(epic));

        assertEquals(201, response.statusCode(),"Статус ответа не 201 (Created)");
        assertEquals(1, manager.getAllEpics().size(),"Эпик не сохранился в менеджере");
    }

    @Test
    public void testGetEpicSubtasks() throws Exception {
        Epic epic = new Epic(1,"Эпик 1", "Описание эпика 1", Status.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2,"Подзадача 1", "Описание подзадачи 1", Status.NEW, epic.getId(), null, null);
        manager.createSubtask(subtask);

        HttpResponse<String> response = sendRequest("/epics/1/subtasks", "GET", null);

        assertEquals(200, response.statusCode(),"Статус ответа не 200 (OK)");
        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(1, subtasks.length, "Подзадача не найдена");
    }

    @Test
    public void testGetChangedEpicStatus () throws Exception {
        Epic epic = new Epic(1,"Эпик 1", "Описание эпика 1", Status.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask(0,"Подзадача 1", "Описание подзадачи 1", Status.DONE,
                epic.getId(), null, null);

        HttpResponse<String> response = sendRequest("/subtasks", "POST", gson.toJson(subtask));

        assertEquals(201, response.statusCode());
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).get().getStatus());
    }
}