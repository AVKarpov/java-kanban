import manager.Managers;
import manager.TaskManager;
import tasks.*;

import java.util.List;

import static tasks.TaskStatus.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        //Создайте 2 задачи
        Task task1 = new Task("Task #1","Task #1 description", NEW);
        int task1Id = manager.addNewTask(task1);
        Task task2 = new Task("Task #2","Task #2 description", IN_PROGRESS);
        int task2Id = manager.addNewTask(task2);

        //Создайте эпик с 3 подзадачами
        Epic epic1 = new Epic("Epic #1","Epic #1 description", NEW);
        int epic1Id = manager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Epic #1 subTask #1","Epic #1 subTask #1 description", NEW, epic1Id);
        SubTask subTask2 = new SubTask("Epic #1 subTask #2","Epic #1 subTask #2 description", NEW, epic1Id);
        SubTask subTask3 = new SubTask("Epic #1 subTask #3","Epic #1 subTask #3 description", IN_PROGRESS, epic1Id);
        int subTask1Id = manager.addNewSubTask(subTask1);
        int subTask2Id = manager.addNewSubTask(subTask2);
        int subTask3Id = manager.addNewSubTask(subTask3);

        //Создайте эпик без подзадач
        Epic epic2 = new Epic("Epic #2","Epic #2 description", NEW);
        int epic2Id = manager.addNewEpic(epic2);

        //запросите созданные задачи несколько раз в разном порядке
        manager.getTask(task1Id);
        manager.getTask(task2Id);
        manager.getTask(task1Id);
        manager.getSubTask(subTask1Id);
        manager.getEpic(epic1Id);
        manager.getEpic(epic2Id);
        manager.getTask(task2Id);
        printHistory(manager);

        manager.getEpic(epic1Id);
        manager.getEpic(epic2Id);
        manager.getTask(task2Id);
        manager.getTask(task1Id);
        manager.getSubTask(subTask1Id);
        manager.getSubTask(subTask2Id);
        manager.getSubTask(subTask3Id);
        manager.getTask(task1Id);
        manager.getTask(task2Id);
        printHistory(manager);

        manager.deleteTask(task1Id);
        printHistory(manager);

        manager.deleteEpic(epic1Id);
        printHistory(manager);
    }

    static void printHistory(TaskManager manager) {
        System.out.println("--HISTORY BEGIN--");
        List<Task> taskHistory = manager.getHistory();
        int i = 0;
        for (Task k : taskHistory) {
            System.out.print(++i + ": ");
            System.out.println(k.toString());
        }
        System.out.println("--HISTORY END--");
    }
    static void printAll(TaskManager manager) {
        System.out.println("Список всех задач:");
        printAllTasks(manager);
        System.out.println("Список всех подзадач:");
        printAllSubTasks(manager);
        System.out.println("Список всех эпиков:");
        printAllEpics(manager);
    }
    static void printAllTasks(TaskManager manager) {
        for (Task t : manager.getAllTasks()) {
            System.out.println(t);
        }
    }

    static void printAllSubTasks(TaskManager manager) {
        for (SubTask st : manager.getAllSubTasks()) {
            System.out.println(st);
        }
    }

    static void printAllEpics(TaskManager manager) {
        for (Epic e : manager.getAllEpics()) {
            System.out.println(e);
        }
    }

    static void printEpicSubTasksStatus(TaskManager manager, Epic epic) {
        for (SubTask st : manager.getEpicSubTasks(epic.getId())) {
            System.out.println("Статус подзадачи " + st.getName() + ": " + st.getStatus());
        }
    }
}
