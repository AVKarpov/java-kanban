package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class Epic extends Task {

    protected ArrayList<Integer> subTaskIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus, TaskType.EPIC);
        //this.endTime = startTime;
    }

    public Epic(String name, String description, TaskStatus taskStatus, LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(name, description, taskStatus, TaskType.EPIC, startTime, duration);
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
