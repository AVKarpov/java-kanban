package manager;

import tasks.Epic;
import tasks.TaskStatus;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

import static tasks.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer,Task> tasks = new HashMap<>();
    protected final Map<Integer,SubTask> subTasks = new HashMap<>();
    protected final Map<Integer,Epic> epics = new HashMap<>();

    Comparator<Task> priorityComparator = Comparator
            .comparing(Task::getStartTime,Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId,Comparator.naturalOrder());

    protected Set<Task> prioritizedTasks = new TreeSet<>(priorityComparator);

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private int generatorId = 0;

    public void setGeneratorId(int generatorId) {
        this.generatorId = generatorId;
    }
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
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int addNewTask(Task task) {
        if (task != null) {
            final int id = ++generatorId;
            task.setId(id);

            if (!isIntersect(task)) {
                    tasks.put(id, task);
                    prioritizedTasks.add(task);
                    return id;
            }
        }
        return -1;
    }

    private boolean isIntersect(Task task) {
        final LocalDateTime startTime = task.getStartTime();
        final LocalDateTime endTime = task.getEndTime();

        if (startTime != null && endTime != null) {
            if (prioritizedTasks.size() > 0)
                for (Task t : prioritizedTasks) {
                    if (startTime.isAfter(t.getStartTime()) && endTime.isBefore(t.getEndTime())) {
                        return true;
                    }
                }
        }
        return false;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        if (subTask != null) {
            Epic epic = getEpic(subTask.getEpicId());

            if (epic != null && !isIntersect(subTask)) {
                final int id = ++generatorId;
                subTask.setId(id);
                epic.addSubTaskId(id);
                prioritizedTasks.add(subTask);
                subTasks.put(id, subTask);
                updateEpicStatus(epic);
                updateEpicDuration(epic);
                return id;
            }
        }
        return -1;
    }

    private void updateEpicDuration(Epic epic) {
        if (epic != null) {
            ArrayList<Integer> ids = epic.getSubTaskIds();
            if (ids.size() > 0) {
                long epicDuration = 0;
                LocalDateTime epicStartTime = subTasks.get(ids.get(0)).getStartTime();
                LocalDateTime epicEndTime = subTasks.get(ids.get(0)).getEndTime();
                for (Integer i : ids) {
                    epicDuration += subTasks.get(i).getDuration();
                    if (epicStartTime != null && subTasks.get(i).getStartTime() != null)
                        if (epicStartTime.isAfter(subTasks.get(i).getStartTime()))
                            epicStartTime = subTasks.get(i).getStartTime();
                    if (epicEndTime != null && subTasks.get(i).getEndTime() != null)
                        if (epicEndTime.isBefore(subTasks.get(i).getEndTime()))
                            epicEndTime = subTasks.get(i).getEndTime();
                }
                epic.setDuration(epicDuration);
                epic.setStartTime(epicStartTime);
                epic.setEndTime(epicEndTime);
            }
        }
    }

    private void updateEpicStatus(Epic epic) {
        Set<TaskStatus> subTasksTaskStatuses = new HashSet<>();
        for (Integer i : epic.getSubTaskIds())
            subTasksTaskStatuses.add(subTasks.get(i).getStatus());

        //если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW
        if ((epic.getSubTaskIds().size() == 0) || (subTasksTaskStatuses.contains(NEW) && subTasksTaskStatuses.size() == 1)) {
            epic.setStatus(NEW);
            return;
        }
        //если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE
        if (subTasksTaskStatuses.contains(DONE) && subTasksTaskStatuses.size() == 1) {
            epic.setStatus(DONE);
            return;
        }
        //во всех остальных случаях статус должен быть IN_PROGRESS
        epic.setStatus(IN_PROGRESS);
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
        if (task != null)
            if (!isIntersect(task))
                tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null)
            //проверяем, что у нас есть такая сабтаска
            if (subTasks.get(subTask.getId()) != null) {
                if (!isIntersect(subTask)) {
                    //обновляем сабтаску
                    subTasks.put(subTask.getId(), subTask);
                    //обновляем статус эпика
                    updateEpicStatus(epics.get(subTask.getEpicId()));
                    //обновляем время эпика
                    updateEpicDuration(epics.get(subTask.getEpicId()));
                }
            }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet())
            historyManager.remove(id);
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        //проверяем, что такая сабтаска существует
        if (subTasks.get(id) != null) {
            int epicId = subTasks.get(id).getEpicId();
            prioritizedTasks.remove(subTasks.get(id));
            //удаляем сабтаску
            subTasks.remove(id);
            epics.get(epicId).getSubTaskIds().remove((Integer) id);
            historyManager.remove(id);
            //обновляем статус эпика
            updateEpicStatus(epics.get(epicId));
            //обновляем время эпика
            updateEpicDuration(epics.get(epicId));
        }
    }

    @Override
    public void deleteEpic(int id) {
        //проверяем, что такой эпик существует
        if (epics.containsKey(id)) {
            //удаляем все сабтаски эпика, если имеются
            if (epics.get(id).getSubTaskIds().size() > 0)
                for (Integer i : epics.get(id).getSubTaskIds()) {
                    subTasks.remove(i);
                    historyManager.remove(i);
                }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subTasks.values());

        return prioritizedTasks;
    }
}
