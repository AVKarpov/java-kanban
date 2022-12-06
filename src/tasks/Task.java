package tasks;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus;
    protected TaskType taskType = TaskType.TASK;
    protected LocalDateTime startTime;
    protected long duration; //in minutes

    public Task(String name, String description, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        //this.duration = 0L;
        //this.startTime = null; //LocalDateTime.now();
    }

    public Task(String name, String description, TaskStatus taskStatus, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus taskStatus, TaskType taskType, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus taskStatus, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
        //this.duration = 0L;
        //this.startTime = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return (startTime != null) ? startTime.plusMinutes(duration) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && Objects.equals(taskStatus, task.taskStatus) && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskStatus, taskType, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + taskStatus + '\'' +
                ", type='" + taskType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                '}';
    }
}
