package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetHistory(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getHistory());
        sendSuccess(exchange, response);
    }
}