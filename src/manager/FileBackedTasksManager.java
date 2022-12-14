package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static tasks.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,startTime,duration,endTime,epic\n");

            ArrayList<Task> tasks = super.getAllTasks();
            if (tasks.size() > 0)
                for (Task task : tasks) {
                    writer.write(CSVTaskFormat.toString(task) + "\n");
                }

            ArrayList<SubTask> subTasks = super.getAllSubTasks();
            if (subTasks.size() > 0)
                for (SubTask subTask : subTasks) {
                    writer.write(CSVTaskFormat.toString(subTask)  + "\n");
                }

            ArrayList<Epic> epics = super.getAllEpics();
            if (epics.size() > 0)
                for (Epic epic : epics) {
                    writer.write(CSVTaskFormat.toString(epic)  + "\n");
                }
            writer.write("\n");

            String result = CSVTaskFormat.historyToString(super.historyManager);
            if (result != null)
               writer.write(result);

        } catch (IOException e) {
            throw new ManagerSaveException("FileBackedTasksManager: Возникло исключение при сохранении в файл");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);

        try {
            String content = Files.readString(Path.of(file.getPath()));
            if (!content.isEmpty()) {
                String[] lines = content.split("\r?\n");
                int generatorId = 0;
                int i = 1;

                if (i < lines.length) {
                    while (!lines[i].equals("")) {
                        Task task = CSVTaskFormat.fromString(lines[i]);
                        if (task != null) {
                            if (task.getId() > generatorId)
                                generatorId = task.getId();
                            taskManager.restoreTaskInMemory(task);
                        }
                        i++;
                        if (i == lines.length)
                            break;
                    }
                    taskManager.updateEpicsSubTaskIds();
                    taskManager.setGeneratorId(generatorId);
                }
                //restore History
                if (i < lines.length) {
                    List<Integer> historyIds = CSVTaskFormat.historyFromString(lines[i + 1]);
                    if (historyIds != null)
                        taskManager.restoreHistory(historyIds);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("FileBackedTasksManager: Возникло исключение при загрузке из файла");
        }

        return taskManager;
    }

    protected void updateEpicsSubTaskIds() {
        for (SubTask subTask : super.subTasks.values()) {
            Epic epic = super.epics.get(subTask.getEpicId());
            if (epic != null)
                epic.addSubTaskId(subTask.getId());
        }
    }

    public void setGeneratorId(int generatorId) {
        super.setGeneratorId(generatorId);
    }

    protected void restoreHistory(List<Integer> historyIds) {
        for (Integer id : historyIds) {
            if (super.tasks.get(id) != null) {
                super.historyManager.add(tasks.get(id));
            } else if (super.subTasks.get(id) != null) {
                super.historyManager.add(subTasks.get(id));
            } else if (super.epics.get(id) != null) {
                super.historyManager.add(epics.get(id));
            }
        }
    }

    protected void restoreTaskInMemory(Task task) {
        if (task.getTaskType() == TASK) {
            super.tasks.put(task.getId(),task);
        } else if (task.getTaskType() == SUBTASK) {
            super.subTasks.put(task.getId(),(SubTask) task);
        } else if (task.getTaskType() == EPIC) {
            super.epics.put(task.getId(), (Epic) task);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = super.getAllTasks();
        save();
        return tasks;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subTasks = super.getAllSubTasks();
        save();
        return subTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = super.getAllEpics();
        save();
        return epics;
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int epicId) {
        ArrayList<SubTask> subTasks = super.getEpicSubTasks(epicId);
        save();
        return subTasks;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        int id = super.addNewSubTask(subTask);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
