package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.service.TaskManager;

import java.io.IOException;

public class EpicSubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicSubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");
                if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                    handleGetEpicSubtasks(exchange, pathParts[2]);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String epicIdStr) throws IOException {
        try {
            int epicId = Integer.parseInt(epicIdStr);
            String response = gson.toJson(taskManager.getSubtasksByEpicId(epicId));
            sendSuccess(exchange, response);
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}