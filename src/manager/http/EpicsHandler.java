package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.ManagerSaveException;
import manager.model.Epic;
import manager.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetAllEpics(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetEpicById(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateEpic(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteEpic(exchange, pathParts[2]);
                    } else if (pathParts.length == 2) {
                        handleDeleteAllEpics(exchange);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllEpics());
        sendSuccess(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            Optional<Epic> epic = taskManager.getEpicById(id);
            if (epic.isPresent()) {
                String response = gson.toJson(epic.get());
                sendSuccess(exchange, response);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        Epic epic = gson.fromJson(requestBody, Epic.class);
        try {
            if (epic.getId() == 0) {
                taskManager.createEpic(epic);
                sendCreated(exchange, gson.toJson(epic));
            } else {
                taskManager.updateEpic(epic);
                sendCreated(exchange, gson.toJson(epic));
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            taskManager.deleteEpicById(id);
            sendSuccess(exchange, "Эпик удален");
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        sendSuccess(exchange, "Все эпики удалены");
    }
}