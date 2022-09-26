package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TasksManager implements ITasksManager {

    private HashMap<Integer,Task> tasks = new HashMap<>();
    private HashMap<Integer,SubTask> subTasks = new HashMap<>();
    private HashMap<Integer,Epic> epics = new HashMap<>();

    private int generatorId = 0;

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int epicId) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        for (Integer i : epics.get(epicId).getSubTaskIds())
            subTaskList.add(subTasks.get(i));
        return subTaskList;
    }

    @Override
    public Task getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    @Override
    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id,task);
        return id;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        Epic epic = getEpic(subTask.getEpicId());
        if (epic == null) {
            System.out.println("Can not add " + subTask.getName() + ": no find tasks.Epic" + subTask.getEpicId());
            return -1;
        } else {
            final int id = ++generatorId;
            subTask.setId(id);
            epic.addSubTaskId(id);
            subTasks.put(id,subTask);
            updateEpicStatus(epic);
            return id;
        }
    }

    private void updateEpicStatus(Epic epic) {
        Set<String> subTasksStatus = new HashSet<>();
        for (Integer i : epic.getSubTaskIds())
            subTasksStatus.add(subTasks.get(i).getStatus());

        //если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW
        if ((epic.getSubTaskIds().size() == 0) || (subTasksStatus.contains("NEW") && subTasksStatus.size() == 1)) {
            epic.setStatus("NEW");
            return;
        }
        //если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE
        if (subTasksStatus.contains("DONE") && subTasksStatus.size() == 1) {
            epic.setStatus("DONE");
            return;
        }
        //во всех остальных случаях статус должен быть IN_PROGRESS
        epic.setStatus("IN_PROGRESS");
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id,epic);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        //проверяем, что у нас есть такая сабтаска
        if (subTasks.get(subTask.getId()) != null) {
            //обновляем сабтаску
            subTasks.put(subTask.getId(), subTask);
            //обновляем статус эпика
            updateEpicStatus(epics.get(subTask.getEpicId()));
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        //проверяем, что такая сабтаска существует
        if (subTasks.get(id) != null) {
            int epicId = subTasks.get(id).getEpicId();
            //удаляем сабтаску
            subTasks.remove(id);
            //обновляем статус эпика
            updateEpicStatus(epics.get(epicId));
        }
    }

    @Override
    public void deleteEpic(int id) {
        //проверяем, что такой эпик существует
        if (epics.containsKey(id)) {
            //удаляем все сабтаски эписка, если имеются
            if (epics.get(id).getSubTaskIds().size() > 0)
                for (Integer i : epics.get(id).getSubTaskIds())
                    subTasks.remove(i);
            epics.remove(id);
        }
    }
}
