package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import manager.Managers;
import tasks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(String url) {
        this(url, false);
    }

    public HttpTaskManager(String url, boolean hasLoad) {
        super(null);
        this.gson = Managers.getGson();
        this.client = new KVTaskClient(url);
        if (hasLoad)
            loadFromServer();
    }

    protected void loadFromServer() {
        ArrayList<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>(){}.getType());
        for (Task task : tasks) {
            super.restoreTaskInMemory(task);
        }

        ArrayList<SubTask> subTasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<SubTask>>(){}.getType());
        for (SubTask subTask : subTasks) {
            super.restoreTaskInMemory(subTask);
        }

        ArrayList<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>(){}.getType());
        for (Epic epic : epics) {
            super.restoreTaskInMemory(epic);
        }

        super.updateEpicsSubTaskIds();
        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>(){}.getType());
        super.restoreHistory(history);
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);
        String jsonSubTasks = gson.toJson(new ArrayList<>(subTasks.values()));
        client.put("subtasks", jsonSubTasks);
        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics", jsonEpics);

        String jsonHistory = gson.toJson(historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }

}
