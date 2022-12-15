package tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskManager;
import http.HttpTaskServer;
import manager.*;
import org.junit.jupiter.api.*;
import server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.NEW;

public class HttpTaskManagerTest  {
    private TaskManager httpTaskManager;
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private final String url = "http://localhost:8080/";
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(httpTaskManager);
        httpTaskServer.start();
        gson = Managers.getGson();
    }

    @AfterEach
    public void tearDown() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void getAllTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,11,14,20,30),35);
        Task task2 = new Task("Task #2","Task #2 description", NEW,
                LocalDateTime.of(2022,10,11,22,30),30);
        httpTaskManager.addNewTask(task1);
        httpTaskManager.addNewTask(task2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .GET()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(tasks, gson.fromJson(actual.body(),new TypeToken<ArrayList<Task>>(){}.getType()),
                "Список task не соответсвуют друг другу");
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,11,14,20,30),35);
        httpTaskManager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task?id=1"))
                .GET()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(task, gson.fromJson(actual.body(),Task.class), "task не соответсвуют друг другу");
    }

    @Test
    public void addNewTaskTest() throws IOException, InterruptedException {
        assertEquals(0, httpTaskManager.getAllTasks().size(), "В менеджере уже есть задачи");
        Task task = new Task("Task #1","Task #1 description", NEW,
        LocalDateTime.of(2022,11,14,20,30),35);

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .POST(body)
                .build();
        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(1, httpTaskManager.getAllTasks().size(), "Количество задач в менеджере не совпадает");
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,11,14,20,30),35);
        httpTaskManager.addNewTask(task);
        task.setName("Task #1 updated");

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task?id=1"))
                .POST(body)
                .build();
        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(task,httpTaskManager.getTask(task.getId()),"task не обновилась");
    }

    @Test
    public void deleteTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,11,14,20,30),35);
        httpTaskManager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(0,httpTaskManager.getAllTasks().size(),"task не удалён.");
    }

    @Test
    public void deleteAllTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,11,14,20,30),35);
        Task task2 = new Task("Task #2","Task #2 description", NEW,
                LocalDateTime.of(2022,10,11,22,30),30);
        httpTaskManager.addNewTask(task1);
        httpTaskManager.addNewTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .DELETE()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(0,httpTaskManager.getAllTasks().size(),"task не удалены.");
    }

    @Test
    public void getSubTaskByIdTest() throws IOException, InterruptedException {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = httpTaskManager.addNewEpic(epic);
        final SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,10,27,15,0),25);
        httpTaskManager.addNewSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask/subtask?id=2"))
                .GET()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(subTask1, gson.fromJson(actual.body(),SubTask.class), "subtask не соответсвуют друг другу");
    }

    @Test
    public void addNewSubTaskTest() throws IOException, InterruptedException {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = httpTaskManager.addNewEpic(epic);
        final SubTask subTask = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,10,27,15,0),25);

        assertEquals(0, httpTaskManager.getEpicSubTasks(epicId).size(), "В epic уже есть subtask");

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(subTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask/subtask"))
                .POST(body)
                .build();
        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(1, httpTaskManager.getEpicSubTasks(epicId).size(), "Количество subtask в менеджере не совпадает");
    }

    @Test
    public void deleteSubTaskByIdTest() throws IOException, InterruptedException {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = httpTaskManager.addNewEpic(epic);
        final SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,10,27,15,0),25);
        httpTaskManager.addNewSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask/subtask?id=2"))
                .DELETE()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(0,httpTaskManager.getAllSubTasks().size(),"subtask не удалён");
    }

    @Test
    public void getAllEpicsTest() throws IOException, InterruptedException {
        final Epic epic1 = new Epic("Epic #1","Epic #1 description", NEW);
        final Epic epic2 = new Epic("Epic #2","Epic #2 description", NEW);
        httpTaskManager.addNewEpic(epic1);
        httpTaskManager.addNewEpic(epic2);
        ArrayList<Epic> expectedEpics = new ArrayList<>();
        expectedEpics.add(epic1);
        expectedEpics.add(epic2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic"))
                .GET()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(expectedEpics, gson.fromJson(actual.body(),new TypeToken<ArrayList<Epic>>(){}.getType()),
                "Список epic не соответсвуют друг другу");
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        httpTaskManager.addNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic?id=1"))
                .GET()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(epic, gson.fromJson(actual.body(),Epic.class), "epic не соответсвуют друг другу");
    }

    @Test
    public void addNewEpicTest() throws IOException, InterruptedException {
        assertEquals(0, httpTaskManager.getAllEpics().size(), "В менеджере уже есть epic");
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);

        HttpClient client = HttpClient.newHttpClient();
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic"))
                .POST(body)
                .build();
        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(1, httpTaskManager.getAllEpics().size(), "Количество epic в менеджере не совпадает");
    }

    @Test
    public void deleteEpicByIdTest() throws IOException, InterruptedException {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        httpTaskManager.addNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/epic?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(0,httpTaskManager.getAllEpics().size(),"Epic не удалён");
    }

    @Test
    public void getEpicSubTasksTest() throws IOException, InterruptedException {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = httpTaskManager.addNewEpic(epic);
        final SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,10,27,15,0),25);
        final SubTask subTask2 = new SubTask("Epic #1 subTask #2","Epic #1 subTask #2 description", NEW, epicId,
                LocalDateTime.of(2022,11,7,10,0),20);
        httpTaskManager.addNewSubTask(subTask1);
        httpTaskManager.addNewSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/subtask/epic?id=1"))
                .GET()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(httpTaskManager.getEpicSubTasks(epicId),
                gson.fromJson(actual.body(),new TypeToken<ArrayList<SubTask>>(){}.getType()),
                "subtask в epic не соответсвуют");
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task #1","Task #1 description", NEW);
        Task task2 = new Task("Task #2","Task #2 description", NEW);
        httpTaskManager.addNewTask(task1);
        httpTaskManager.addNewTask(task2);
        httpTaskManager.getTask(task1.getId());
        List<Task> expectedHistory = httpTaskManager.getHistory();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/history"))
                .GET()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(expectedHistory, gson.fromJson(actual.body(),new TypeToken<ArrayList<Task>>(){}.getType()),
                "История не совпадает");
    }

    @Test
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,12,6,10,0), 10);
        Task task2 = new Task("Task #2","Task #2 description", NEW,
                LocalDateTime.of(2022,12,9,12,0), 15);
        Task task3 = new Task("Task #3","Task #3 description", NEW,
                LocalDateTime.of(2022,8,9,12,0), 20);
        Task task4 = new Task("Task #4","Task #4 description", NEW,
                LocalDateTime.of(2022,8,9,10,0), 25);

        createEpicAndSubtasks();
        httpTaskManager.addNewTask(task1);
        httpTaskManager.addNewTask(task2);
        httpTaskManager.addNewTask(task3);
        httpTaskManager.addNewTask(task4);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/"))
                .GET()
                .build();

        HttpResponse<String> actual = client.send(testRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,actual.statusCode(),"Получен некорректный статус-код");
        assertEquals(httpTaskManager.getPrioritizedTasks(),
                gson.fromJson(actual.body(), new TypeToken<Set<Task>>(){}.getType()),
                "Prioritized tasks не соответсвуют друг другу");
    }

    private void createEpicAndSubtasks() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = httpTaskManager.addNewEpic(epic);
        final SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30);
        httpTaskManager.addNewSubTask(subTask1);
        final SubTask subTask2 = new SubTask("Epic #1 subTask #2","Epic #1 subTask #2 description", NEW, epicId,
                LocalDateTime.of(2022,10,4,12,0), 20);
        httpTaskManager.addNewSubTask(subTask2);
    }

    @Test
    public void loadFromServerTest() {
        Task task1 = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,11,14,20,30),35);
        Task task2 = new Task("Task #2","Task #2 description", NEW,
                LocalDateTime.of(2022,10,11,22,30),30);
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);

        httpTaskManager.addNewTask(task1);
        httpTaskManager.addNewTask(task2);
        httpTaskManager.addNewEpic(epic);

        httpTaskManager.getTask(1);
        httpTaskManager.getEpic(3);

        HttpTaskManager taskManager = new HttpTaskManager(8078,true);
        assertEquals(httpTaskManager.getAllTasks(), taskManager.getAllTasks(),"Список task не совпадает");
        assertEquals(httpTaskManager.getAllEpics(), taskManager.getAllEpics(),"Список epic не совпадает");
        assertEquals(httpTaskManager.getHistory(), taskManager.getHistory(),"История не совпадает");
    }

}
