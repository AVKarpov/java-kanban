package file;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import tasks.TaskManagerTest;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.NEW;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private File file;

    @BeforeEach
    public void setUp() {
        file = new File("resources/test_"+System.nanoTime()+".csv");
        taskManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    public void tearDown() {
        assertTrue(file.delete());
    }

    @Test
    public void loadFromFile() {
        Task task1 = new Task("Task #1","Task #1 description", NEW,
                LocalDateTime.of(2022,11,14,20,30),35);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Task #2","Task #2 description", NEW,
                LocalDateTime.of(2022,12,5,22,30),40);
        taskManager.addNewTask(task2);
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        int epicId = taskManager.addNewEpic(epic);
        final SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epicId,
                LocalDateTime.of(2022,10,27,15,0),25);
        taskManager.addNewSubTask(subTask1);
        final SubTask subTask2 = new SubTask("Epic #1 subTask #2","Epic #1 subTask #2 description", NEW, epicId,
                LocalDateTime.of(2022,9,12,10,0),45);
        taskManager.addNewSubTask(subTask2);
        taskManager.getTask(2);
        taskManager.getTask(1);
        List<Task> expected = taskManager.getHistory();

        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(file);
        final List<Task> tasks = taskManager2.getAllTasks();
        assertNotNull(tasks,"Возвращает не пустой список задач");
        assertEquals(2,tasks.size(),"Количество задач не совпадает");
        assertEquals(epic,taskManager2.getAllEpics().get(0),"Эпики не совпадают");
        assertEquals(subTask1,taskManager2.getAllSubTasks().get(0),"Подзадачи не совпадают");
        assertEquals(subTask2,taskManager2.getAllSubTasks().get(1),"Подзадачи не совпадают");

        List<Task> actual = taskManager2.getHistory();
        assertEquals(expected, actual,"История не совпадает");
    }

//    a. Пустой список задач.
    @Test
    public void emptyTaskListTest() {
        taskManager.getAllTasks();
        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(file);
        final List<Task> tasks = taskManager2.getAllTasks();
        assertNotNull(tasks,"Возвращает не пустой список задач");
        assertEquals(taskManager.getAllTasks().size(),tasks.size(),"Количество задач не совпадает");
    }

//    b. Эпик без подзадач.
    @Test
    public void epicWithoutSubtasksTest() {
        final Epic epic = new Epic("Epic #1","Epic #1 description", NEW);
        taskManager.addNewEpic(epic);
        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(file);
        final List<Epic> epics = taskManager2.getAllEpics();
        assertEquals(taskManager.getAllEpics().size(),epics.size(),"Количество эпиков не совпадает");
        assertEquals(epic,epics.get(0),"Эпики не совпадают");
        assertEquals(0,epics.get(0).getSubTaskIds().size(),"У эпика из файла есть подзадачи");
    }

//    c. Пустой список истории.
    @Test
    public void emptyHistoryTest() {
        Task task = new Task("Task #1","Task #1 description", NEW);
        taskManager.addNewTask(task);
        List<Task> history = taskManager.getHistory();
        assertEquals(0,history.size(),"В истории есть задачи");

        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(file);
        final List<Task> tasks = taskManager2.getAllTasks();
        assertEquals(history.size(),taskManager2.getHistory().size(),"В истории из файла есть задачи");
    }

}
