package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskManager;

public class Managers {

    private Managers(){}
    public static TaskManager getDefault() { return new HttpTaskManager("http://localhost:8078/"); }
//    public static TaskManager getDefault() { return new FileBackedTasksManager(new File("resources/task.csv")); }

    public static HistoryManager getDefaultHistory() { return new InMemoryHistoryManager(); }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

}
