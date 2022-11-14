package tasks;

public class SubTask extends Task {

    protected int epicId;

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status, TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", subTaskId=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + taskStatus + '\'' +
                '}';
    }
}
