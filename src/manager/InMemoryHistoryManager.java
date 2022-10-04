package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_SIZE = 10;

    private final List<Task> historyTasks = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return historyTasks;
    }

    @Override
    public void addTask(Task task) {
        if (historyTasks.size() >= HISTORY_SIZE)
            historyTasks.remove(0);
        historyTasks.add(task);
    }
}
