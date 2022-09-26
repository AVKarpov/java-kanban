package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    public void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "subTaskIds=" + subTaskIds +
                ", epicId=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}' + "\n";
    }
}
