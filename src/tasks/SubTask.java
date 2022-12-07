package tasks;

import java.time.LocalDateTime;

public class SubTask extends Task {

    protected int epicId;

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status, TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicId, LocalDateTime startTime, long duration) {
        super(name, description, status, TaskType.SUBTASK, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", subTaskId=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + taskStatus + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                '}';
    }
}
