package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.ManagerSaveException;
import manager.model.Task;
import manager.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetAllTasks(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetTaskById(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateTask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteTask(exchange, pathParts[2]);
                    } else if (pathParts.length == 2) {
                        handleDeleteAllTasks(exchange);
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

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTasks());
        sendSuccess(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            Optional<Task> task = taskManager.getTaskById(id);
            if (task.isPresent()) {
                String response = gson.toJson(task.get());
                sendSuccess(exchange, response);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        Task task = gson.fromJson(requestBody, Task.class);
        try {
            if (task.getId() == 0) {
                taskManager.createTask(task);
                sendCreated(exchange, gson.toJson(task));
            } else {
                taskManager.updateTask(task);
                sendCreated(exchange, gson.toJson(task));
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            taskManager.deleteTaskById(id);
            sendSuccess(exchange, "Задача удалена");
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        sendSuccess(exchange, "Все задачи удалены");
    }
}