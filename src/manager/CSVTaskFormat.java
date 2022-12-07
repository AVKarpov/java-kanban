package manager;

import tasks.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static tasks.TaskType.*;

/*
id,type,name,status,description,startTime,duration,endTime,epic
1,TASK,Task1,NEW,Description task1,
2,EPIC,Epic2,DONE,Description epic2,
3,SUBTASK,Sub Task2,DONE,Description sub task3,2

2,3
*/

public class CSVTaskFormat {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static String toString(Task task) {
        if (task != null) {
            String startTime = (task.getStartTime() == null) ? "" : task.getStartTime().format(formatter);
            String endTime = (task.getEndTime() == null) ? "" : task.getEndTime().format(formatter);
            String duration = (task.getDuration() == 0) ? "" : String.valueOf(task.getDuration());

            if (task.getTaskType() == SUBTASK) {
                SubTask subTask = (SubTask) task;
                return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                        + task.getDescription() + "," + startTime + "," + duration + ","
                        + endTime + "," + subTask.getEpicId();
            } else
                return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                        + task.getDescription() + "," + startTime + "," + duration + ","
                    + endTime;
        }
        return "";
    }

    public static Task fromString(String value) {
        if (!value.isEmpty()) {
            final String[] values = value.split(",");
            final int id = Integer.parseInt(values[0]);
            final TaskType type = TaskType.valueOf(values[1]);
            final String name = values[2];
            final TaskStatus taskStatus = TaskStatus.valueOf(values[3]);
            final String description = values[4];

            final LocalDateTime startTime = (values.length > 5) ? LocalDateTime.parse(values[5], formatter) : null;
            final long duration = (values.length > 6) ? Long.parseLong(values[6]) : 0;
            final LocalDateTime endTime = (values.length > 7) ? LocalDateTime.parse(values[7], formatter) : null;

            if (type == TASK) {
                Task task = new Task(name, description, taskStatus, startTime, duration);
                task.setId(id);
                return task;
            } else if (type == SUBTASK) {
                int epicId = 0;
                if (values.length > 5)
                    epicId = Integer.parseInt(values[8]);
                SubTask subTask = new SubTask(name, description, taskStatus, epicId, startTime, duration);
                subTask.setId(id);
                return subTask;
            } else if (type == EPIC) {
                Epic epic = new Epic(name, description, taskStatus, startTime, duration, endTime);
                epic.setId(id);
                return epic;
            }
        }
        return null;
    }

    public static String historyToString(HistoryManager manager) {
        if (manager != null) {
            List<Task> tasks = manager.getHistory();
            StringBuilder result = new StringBuilder();

            if (tasks.size() > 0) {
                for (Task task : tasks) {
                    result.append(task.getId());
                    result.append(",");
                }
                //delete last comma
                if (result.length() > 0)
                 result.deleteCharAt(result.length() - 1);
             }
            return result.toString();
        }
        return null;
    }

    public static List<Integer> historyFromString(String value) {
        if (!value.isEmpty()) {
            List<Integer> historyIds = new ArrayList<>();
            final String[] values = value.split(",");
            for (String ids : values) {
                historyIds.add(Integer.parseInt(ids));
            }
            return historyIds;
        }
        return null;
    }
}
