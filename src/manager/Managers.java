package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskManager;

public class Managers {

    private Managers(){}
    public static TaskManager getDefault() { return new HttpTaskManager(8078); }
    public static HistoryManager getDefaultHistory() { return new InMemoryHistoryManager(); }
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

}
