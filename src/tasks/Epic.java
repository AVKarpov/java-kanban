package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
    }

    public void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIds=" + subTaskIds +
                ", epicId=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + taskStatus + '\'' +
                '}';
    }
}
