package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.ManagerSaveException;
import manager.model.Subtask;
import manager.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetAllSubtasks(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetSubtaskById(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateSubtask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteSubtask(exchange, pathParts[2]);
                    } else if (pathParts.length == 2) {
                        handleDeleteAllSubtasks(exchange);
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

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllSubtasks());
        sendSuccess(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            Optional<Subtask> subtask = taskManager.getSubtaskById(id);
            if (subtask.isPresent()) {
                String response = gson.toJson(subtask.get());
                sendSuccess(exchange, response);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
        try {
            if (subtask.getId() == 0) {
                taskManager.createSubtask(subtask);
                sendCreated(exchange, gson.toJson(subtask));
            } else {
                taskManager.updateSubtask(subtask);
                sendCreated(exchange, gson.toJson(subtask));
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            taskManager.deleteSubtaskById(id);
            sendSuccess(exchange, "Подзадача удалена");
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtasks();
        sendSuccess(exchange, "Все подзадачи удалены");
    }
}