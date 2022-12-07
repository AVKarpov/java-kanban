package tasks;

import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;

public abstract class TaskManagerTest <T extends TaskManager> {
    protected T taskManager;

    @Test
    void getAllTasksTest() {
        Task task1 = new Task("Task #1","Task #1 description", NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Task #2","Task #2 description", NEW);
        taskManager.addNewTask(task2);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        final List<Task> savedTasks = taskManager.getAllTasks();
        assertNotNull(savedTasks,"Список задач не возвращается");
        assertEquals(tasks, savedTasks, "Списки задач не совпадают.");
    }

    @Test
    void getAllSubTasksTest() {
        final Epic epic1 = new Epic("Epic #1","Epic #1 description", NEW);
        int epic1Id = taskManager.addNewEpic(epic1);
        final SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epic1Id,
                LocalDateTime.of(2022,10,5,22,30),30);
        taskManager.addNewSubTask(subTask1);
        final SubTask subTask2 = new SubTask("Epic #1 subTask #2","Epic #1 subTask #2 description", NEW, epic1Id,
                LocalDateTime.of(2022,12,5,22,30),30);
        taskManager.addNewSubTask(subTask2);
        final Epic epic2 = new Epic("Epic #2","Epic #2 description", NEW);
        int epic2Id = taskManager.addNewEpic(epic2);
        final SubTask subTask3 = new SubTask("Epic #2 subTask #1","Epic #2 subTask #1 description", NEW, epic2Id,
                LocalDateTime.of(2022,9,5,22,30),30);
        taskManager.addNewSubTask(subTask3);
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(subTask1);
        subTasks.add(subTask2);
        subTasks.add(subTask3);

        final List<SubTask> savedSubTasks = taskManager.getAllSubTasks();
        assertNotNull(savedSubTasks,"Список подзадач не возвращается");
        assertEquals(subTasks, savedSubTasks, "Подзадачи не совпадают.");
    }

    @Test
    void getAllEpicsTest() {
        final Epic epic1 = new Epic("Epic #1","Epic #1 description", NEW);
        taskManager.addNewEpic(epic1);
        final Epic epic2 = new Epic("Epic #2","Epic #2 description", NEW);
        taskManager.addNewEpic(epic2);
        List<Epic> epics = new ArrayList<>();
        epics.add(epic1);
        epics.add(epic2);

        final List<Epic> savedEpics = taskManager.getAllEpics();
        assertNotNull(savedEpics,"Список эпиков не возвращается");
        assertEquals(epics, savedEpics, "Эпики не совпадают.");
    }

    @Test
    void getTaskTest() {
        Task task = new Task("Task #1", "Task #1 description", NEW,
                LocalDateTime.of(2022,12,5,22,30),30);
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        assertNull(taskManager.getTask(100),"Такая задача существует.");
    }

    @Test
    void getSubTaskTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);
        final SubTask subTask = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30);
        int subTaskId = taskManager.addNewSubTask(subTask);
        final SubTask savedSubTask = taskManager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");

        assertNull(taskManager.getSubTask(100),"Такая подзадача существует.");
    }

    @Test
    void getEpicTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        assertNull(taskManager.getEpic(100),"Такой эпик существует.");
    }

    @Test
    void getEpicSubTasksTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);
        final SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30);
        taskManager.addNewSubTask(subTask1);
        final SubTask subTask2 = new SubTask("Epic #1 subTask #2","Epic #1 subTask #2 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30);
        taskManager.addNewSubTask(subTask2);
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(subTask1);
        subTasks.add(subTask2);

        final List<SubTask> savedSubTasks = taskManager.getEpicSubTasks(epicId);
        assertNotNull(savedSubTasks, "Подзадачи эпика не найдены.");
        assertEquals(subTasks, savedSubTasks, "Подзадачи эпика не совпадают.");
    }

    @Test
    void addNewTaskTest() {
        Task task = new Task("Task #1", "Task #1 description", NEW,
                LocalDateTime.of(2022,12,5,22,30),30);
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> savedTasks = taskManager.getAllTasks();
        assertNotNull(savedTasks, "Задачи на возвращаются.");
        assertEquals(1, savedTasks.size(), "Неверное количество задач.");
        assertEquals(task, savedTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubTaskTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);
        final SubTask subTask = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30);
        int subTaskId = taskManager.addNewSubTask(subTask);

        final SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");
        assertEquals(epicId, subTask.getEpicId(), "Идентификатор эпика в подзадаче не соответсвует идентификатору эпика");

        List<SubTask> savedSubTasks = taskManager.getEpicSubTasks(epicId);
        assertEquals(1, savedSubTasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void addNewEpicTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void updateTaskTest() {
        final int taskId = taskManager.addNewTask(new Task("Task #1", "Task #1 description", NEW,
                LocalDateTime.of(2022,12,5,22,30),30));

        final Task task = taskManager.getTask(taskId);
        task.setName("Task #1 updated");
        task.setDescription("Task #1 updated description");
        task.setStatus(IN_PROGRESS);
        taskManager.updateTask(task);

        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task.getName(), savedTask.getName(), "Имя задачи не совпадает.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задачи не совпадает.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статус задачи не совпадает.");
    }

    @Test
    void updateEpicTest() {
        int epicId = taskManager.addNewEpic(new Epic("Epic #1","Epic #1 description", NEW));

        final Epic epic = taskManager.getEpic(epicId);
        epic.setName("Epic #1 updated");
        epic.setDescription("Epic #1 updated description");
        epic.setStatus(IN_PROGRESS);
        taskManager.updateEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic.getName(), savedEpic.getName(), "Имя эпика не совпадает.");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Описание эпика не совпадает.");
        assertEquals(epic.getStatus(), savedEpic.getStatus(), "Статус эпика не совпадает.");
    }

    @Test
    void updateSubTaskTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);
        int subTaskId = taskManager.addNewSubTask(new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30));

        final SubTask subTask = taskManager.getSubTask(subTaskId);
        subTask.setName("Epic #1 subTask #1 updated");
        subTask.setDescription("Epic #1 subTask #1 updated description");
        subTask.setStatus(IN_PROGRESS);
        taskManager.updateSubTask(subTask);

        final SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask.getName(), savedSubTask.getName(), "Имя подзадачи не совпадает.");
        assertEquals(subTask.getDescription(), savedSubTask.getDescription(), "Описание подзадачи не совпадает.");
        assertEquals(subTask.getStatus(), savedSubTask.getStatus(), "Статус эпика не совпадает.");
    }

    @Test
    void deleteAllTasksTest() {
        Task task1 = new Task("Task #1", "Task #1 description", NEW,
                LocalDateTime.of(2022,12,5,22,30),30);
        Task task2 = new Task("Task #2", "Task #2 description", NEW,
                LocalDateTime.of(2022,12,5,22,30),30);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        assertEquals(2,taskManager.getAllTasks().size(),"Количество задач не совпадает");
        taskManager.deleteAllTasks();
        assertEquals(0,taskManager.getAllTasks().size(),"Количество задач не совпадает");
    }

    @Test
    void deleteTaskTest() {
        Task task1 = new Task("Task #1", "Task #1 description", NEW,
                LocalDateTime.of(2022,12,5,22,30),30);
        Task task2 = new Task("Task #2", "Task #2 description", NEW,
                LocalDateTime.of(2022,12,5,22,30),30);
        taskManager.addNewTask(task1);
        final int task2Id = taskManager.addNewTask(task2);
        assertEquals(2,taskManager.getAllTasks().size(),"Количество задач не совпадает");
        taskManager.deleteTask(task2Id);
        assertEquals(1,taskManager.getAllTasks().size(),"Количество задач не совпадает");
        assertNull(taskManager.getTask(task2Id));
    }

    @Test
    void deleteSubTaskTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);
        int subTaskId = taskManager.addNewSubTask(new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30));

        assertEquals(1,taskManager.getEpicSubTasks(epicId).size(),"Количество подзадач не совпадает");
        taskManager.deleteSubTask(subTaskId);
        assertEquals(0,taskManager.getEpicSubTasks(epicId).size(),"Количество подзадач не совпадает");
        assertNull(taskManager.getSubTask(subTaskId));
    }

    @Test
    void deleteEpicTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);

        assertEquals(1,taskManager.getAllEpics().size(),"Количество эпиков не совпадает");
        taskManager.deleteEpic(epicId);
        assertEquals(0,taskManager.getAllEpics().size(),"Количество эпиков не совпадает");
        assertNull(taskManager.getEpic(epicId));
    }

    @Test
    void updateEpicStatusTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);

        assertEquals(NEW,taskManager.getEpic(epicId).getStatus(),"Статус отличается от NEW");

        SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30);
        SubTask subTask2 = new SubTask("Epic #1 subTask #2","Epic #1 subTask #2 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30);
        taskManager.addNewSubTask(subTask1);
        taskManager.addNewSubTask(subTask2);
        assertEquals(NEW,taskManager.getEpic(epicId).getStatus(),"Статус отличается от NEW");

        subTask1.setStatus(IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        subTask2.setStatus(IN_PROGRESS);
        taskManager.updateSubTask(subTask2);
        assertEquals(IN_PROGRESS,taskManager.getEpic(epicId).getStatus(),"Статус отличается от IN_PROGRESS");

        subTask2.setStatus(DONE);
        taskManager.updateSubTask(subTask2);
        assertEquals(IN_PROGRESS,taskManager.getEpic(epicId).getStatus(),"Статус отличается от IN_PROGRESS");

        subTask1.setStatus(DONE);
        taskManager.updateSubTask(subTask1);
        assertEquals(DONE,taskManager.getEpic(epicId).getStatus(),"Статус отличается от DONE");
    }

    private void createEpicAndSubtasks() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);
        final SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,12,5,22,30),30);
        taskManager.addNewSubTask(subTask1);
        final SubTask subTask2 = new SubTask("Epic #1 subTask #2","Epic #1 subTask #2 description", NEW, epicId,
                LocalDateTime.of(2022,10,4,12,0), 20);
        taskManager.addNewSubTask(subTask2);
    }

    @Test
    void epicStartTimeTest() {
        createEpicAndSubtasks();
        LocalDateTime expected = LocalDateTime.of(2022,10,4,12,0);
        LocalDateTime actual = taskManager.getEpic(1).getStartTime();
        assertEquals(expected, actual,"Время начала эпика не совпадает");
    }

    @Test
    void epicEndTimeTest() {
        createEpicAndSubtasks();
        LocalDateTime expected = LocalDateTime.of(2022,12,5,22,30).plusMinutes(30);
        LocalDateTime actual = taskManager.getEpic(1).getEndTime();
        assertEquals(expected, actual,"Время завершения эпика не совпадает");
    }

    @Test
    void epicDurationTest() {
        createEpicAndSubtasks();
        long expected = 50;
        long actual = taskManager.getEpic(1).getDuration();
        assertEquals(expected, actual,"Время выполнения эпика не совпадает");
    }

    @Test
    void epicUpdateTimingTest() {
        createEpicAndSubtasks();

        long expectedDuration = 50;
        LocalDateTime expectedStartDateTime = LocalDateTime.of(2022,10,4,12,0);
        LocalDateTime expectedEndDateTime = LocalDateTime.of(2022,12,5,22,30).plusMinutes(30);

        long actualDuration = taskManager.getEpic(1).getDuration();
        LocalDateTime actualStartDateTime = taskManager.getEpic(1).getStartTime();
        LocalDateTime actualEndDateTime = taskManager.getEpic(1).getEndTime();

        assertEquals(expectedDuration, actualDuration,"Время выполнения исходного эпика не совпадает");
        assertEquals(expectedStartDateTime, actualStartDateTime,"Время начала исходного эпика не совпадает");
        assertEquals(expectedEndDateTime, actualEndDateTime,"Время завершения исходного эпика не совпадает");

        final SubTask subTask3 = new SubTask("Epic #1 subTask #3","Epic #1 subTask #3 description", NEW, 1,
                LocalDateTime.of(2022,12,10,15,30), 60);
        taskManager.addNewSubTask(subTask3);

        long newExpectedDuration = 110;
        LocalDateTime newExpectedEndDateTime = LocalDateTime.of(2022,12,10,15,30).plusMinutes(60);

        long newActualDuration = taskManager.getEpic(1).getDuration();
        LocalDateTime newActualEndDateTime = taskManager.getEpic(1).getEndTime();

        long actual = taskManager.getEpic(1).getDuration();
        assertEquals(newExpectedDuration, newActualDuration,"Время выполнения обновленного эпика не совпадает");
        assertEquals(newExpectedEndDateTime, newActualEndDateTime,"Время завершения обновленного эпика не совпадает");
    }

    @Test
    void prioritizingTasksSubtasksTest() {
        Task task1 = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,12,6,10,0), 10);
        Task task2 = new Task("Task #2","Task #2 description", NEW,
                LocalDateTime.of(2022,12,9,12,0), 15);
        Task task3 = new Task("Task #3","Task #3 description", NEW,
                LocalDateTime.of(2022,8,9,12,0), 20);
        Task task4 = new Task("Task #4","Task #4 description", NEW,
                LocalDateTime.of(2022,8,9,10,0), 25);

        createEpicAndSubtasks();
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewTask(task4);

        String expected = "[Task{taskId=7, name='Task #4', description='Task #4 description', status='NEW', type='TASK', startTime='2022-08-09T10:00', duration='25', endTime='2022-08-09T10:25'}, Task{taskId=6, name='Task #3', description='Task #3 description', status='NEW', type='TASK', startTime='2022-08-09T12:00', duration='20', endTime='2022-08-09T12:20'}, SubTask{epicId=1, subTaskId=3, name='Epic #1 subTask #2', description='Epic #1 subTask #2 description', status='NEW', startTime='2022-10-04T12:00', duration='20', endTime='2022-10-04T12:20'}, SubTask{epicId=1, subTaskId=2, name='Epic #1 subTask #1', description='Epic #1 subTask #1 description', status='NEW', startTime='2022-12-05T22:30', duration='30', endTime='2022-12-05T23:00'}, Task{taskId=4, name='Task #1', description='Task #1 description', status='NEW', type='TASK', startTime='2022-12-06T10:00', duration='10', endTime='2022-12-06T10:10'}, Task{taskId=5, name='Task #2', description='Task #2 description', status='NEW', type='TASK', startTime='2022-12-09T12:00', duration='15', endTime='2022-12-09T12:15'}]";
        Set<Task> actual = taskManager.getPrioritizedTasks();

        assertEquals(expected, actual.toString(),"Время завершения обновленного эпика не совпадает");
    }

}
