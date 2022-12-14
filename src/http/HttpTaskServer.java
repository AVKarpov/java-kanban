package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/*--- Mapping
GET /tasks/ -> getPrioritizedTasks()
GET /tasks/task/ -> getTasks()
GET /tasks/task?id= -> getTaskById(id)
POST /tasks/task/ body:{task..} -> addTask(task), updateTask(task)
DELETE /tasks/task?id= -> deleteTaskById(id)
DELETE /tasks/task/ -> deleteAllTasks()

GET,POST,DELETE /tasks/subtask/ -> subtask methods
GET /tasks/subtask/epic?id= -> getEpicSubTasks(id)

GET,POST,DELETE /tasks/epic/ -> epic methods
---*/

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final HttpTaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = (HttpTaskManager) taskManager;
        this.gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/", this::taskHandler);
        server.createContext("/tasks/subtask", this::subTaskHandler);
        server.createContext("/tasks/epic", this::epicHandler);
        server.createContext("/tasks/history", this::historyHandler);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        System.out.println("Остановили сервер на порту " + PORT);
        server.stop(0);
    }

    private void taskHandler(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/tasks: " + httpExchange.getRequestURI());
            String path = httpExchange.getRequestURI().getPath().substring("/tasks/".length());
            String query = httpExchange.getRequestURI().getQuery();

            String method = httpExchange.getRequestMethod();
            String response;
            String idParam;
            int id;

            switch (method) {
                case "GET":
                    if (path.equals("")) {
                        response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(httpExchange, response);
                    } else if (path.contains("task")) {
                        if (query == null) {
                            response = gson.toJson(taskManager.getAllTasks());
                            sendText(httpExchange, response);
                        } else {
                            idParam = query.substring("id=".length());
                            if (idParam.isEmpty()) {
                                System.out.println("id для получения task пустой. id указывается в пути: " +
                                        "/tasks/task?id={id}");
                                httpExchange.sendResponseHeaders(400, 0);
                                return;
                            }
                            id = Integer.parseInt(idParam);
                            Task task = taskManager.getTask(id);
                            if (task == null) {
                                System.out.println("task с таким id не найден");
                                httpExchange.sendResponseHeaders(400, 0);
                                return;
                            }
                            response = gson.toJson(task);
                            sendText(httpExchange, response);
                        }
                    }
                    break;

                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Body в task пустой");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    Task task = gson.fromJson(json, Task.class);
                    id = task.getId();
                    if (id != 0) {
                        taskManager.updateTask(task);
                        System.out.println("Task с id = " + id + " обновлена.");
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        taskManager.addNewTask(task);
                        System.out.println("Создали новую task с id = " + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;

                case "DELETE":
                    if (path.equals("task")) {
                        if (query == null) {
                            taskManager.deleteAllTasks();
                            System.out.println("Удаление всех task выполнено успешно!");
                            httpExchange.sendResponseHeaders(200, -1);
                        } else if (query.contains("id=")) {
                            idParam = query.substring("id=".length());
                            if (idParam.isEmpty()) {
                                System.out.println("id для получения task пустой. id указывается в пути: " +
                                        "/tasks/task?id={id}");
                                httpExchange.sendResponseHeaders(400, 0);
                                return;
                            }
                            id = Integer.parseInt(idParam);
                            taskManager.deleteTask(id);
                            System.out.println("Удаление task c id = " + id + " выполнено успешно!");
                            httpExchange.sendResponseHeaders(200, -1);
                        }
                    }
                    break;

                default:
                    System.out.println("\n/tasks ждёт GET,POST,DELETE-запросы, а получил: "
                            + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void subTaskHandler(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/tasks: " + httpExchange.getRequestURI());
            String path = httpExchange.getRequestURI().getPath().substring("/tasks/subtask/".length());
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            String response;
            String idParam;
            int id;

            switch (method) {
                case "GET":
                    switch (path) {
                        case "subtask":
                            if (query == null) {
                                response = gson.toJson(taskManager.getAllSubTasks());
                                sendText(httpExchange, response);
                            } else if (query.contains("id=")){
                                idParam = query.substring("id=".length());
                                if (idParam.isEmpty()) {
                                    System.out.println("id для получения subtask пустой. id указывается в пути: " +
                                            "/tasks/subtask/subtask?id={id}");
                                    httpExchange.sendResponseHeaders(400, 0);
                                    return;
                                }
                                id = Integer.parseInt(idParam);
                                SubTask subTask = taskManager.getSubTask(id);
                                if (subTask == null) {
                                    System.out.println("subtask с таким id не найден");
                                    httpExchange.sendResponseHeaders(400, 0);
                                    return;
                                }
                                response = gson.toJson(subTask);
                                sendText(httpExchange, response);
                            }
                            break;

                        case "epic":
                            if (query != null) {
                                idParam = query.substring("id=".length());
                                if (idParam.isEmpty()) {
                                    System.out.println("id для получения subtask у epic пустой. id указывается в пути: " +
                                            "/tasks/subtask/epic?id={id}");
                                    httpExchange.sendResponseHeaders(400, 0);
                                    return;
                                }
                                id = Integer.parseInt(idParam);
                                List<SubTask> subTasks = taskManager.getEpicSubTasks(id);
                                if (subTasks.size() == 0) {
                                    System.out.println("У epic с таким id не найдены subtask");
                                    httpExchange.sendResponseHeaders(400, 0);
                                    return;
                                }
                                response = gson.toJson(subTasks);
                                sendText(httpExchange, response);
                            }
                            break;
                    }
                    break;

                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Body в subtask пустой");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    SubTask subTask = gson.fromJson(json, SubTask.class);
                    id = subTask.getId();
                    if (id != 0) {
                        taskManager.updateSubTask(subTask);
                        System.out.println("subtask с id = " + id + " обновлена.");
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        id = taskManager.addNewSubTask(subTask);
                        System.out.println("Создали новую subtask с id = " + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;

                case "DELETE":
                    if (path.equals("subtask") && (query != null)) {
                        idParam = query.substring("id=".length());
                        if (idParam.isEmpty()) {
                            System.out.println("id для удаления subtask пустой. id указывается в пути: " +
                                    "/tasks/subtask/subtask?id={id}");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        id = Integer.parseInt(idParam);
                        taskManager.deleteSubTask(id);
                        System.out.println("Удаление subtask c id = " + id + " выполнено успешно!");
                        httpExchange.sendResponseHeaders(200, -1);
                    }
                    break;

                default:
                    System.out.println("\n/tasks ждёт GET,POST,DELETE-запросы, а получил: "
                            + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void epicHandler(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/tasks: " + httpExchange.getRequestURI());
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            String response;
            String idParam;
            int id;

            switch (method) {
                case "GET":
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllEpics());
                        sendText(httpExchange, response);
                    } else if (query.contains("id=")) {
                        idParam = query.substring("id=".length());
                        if (idParam.isEmpty()) {
                            System.out.println("id для получения epic пустой. id указывается в пути: " +
                                    "/tasks/epic?id={id}");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        id = Integer.parseInt(idParam);
                        Epic epic = taskManager.getEpic(id);
                        if (epic == null) {
                            System.out.println("epic с таким id не найден");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        response = gson.toJson(epic);
                        sendText(httpExchange, response);
                    }
                    break;

                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Body в epic пустой");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    Epic epic = gson.fromJson(json, Epic.class);
                    id = epic.getId();
                    if (id != 0) {
                        taskManager.updateEpic(epic);
                        System.out.println("epic с id = " + id + " обновлен.");
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        id = taskManager.addNewEpic(epic);
                        System.out.println("Создали новый epic с id = " + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;

                case "DELETE":
                    if (query != null) {
                        idParam = query.substring("id=".length());
                        if (idParam.isEmpty()) {
                            System.out.println("id для удаления epic пустой. id указывается в пути: " +
                                    "/tasks/epic?id={id}");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        id = Integer.parseInt(idParam);
                        taskManager.deleteEpic(id);
                        System.out.println("Удаление epic c id = " + id + " выполнено успешно!");
                        httpExchange.sendResponseHeaders(200, -1);
                    }
                    break;

                default:
                    System.out.println("\n/tasks ждёт GET,POST,DELETE-запросы, а получил: "
                            + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }

    }

    private void historyHandler(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/tasks/history");
            String method = httpExchange.getRequestMethod();

            if (!"GET".equals(method)) {
                System.out.println("\n/tasks/history ждёт GET-запрос, а получил: " + method);
                httpExchange.sendResponseHeaders(405, 0);
            }
            String response = gson.toJson(taskManager.getHistory());
            sendText(httpExchange, response);
        }
        finally {
            httpExchange.close();
        }
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

}
