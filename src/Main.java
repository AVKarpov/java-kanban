import manager.TasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TasksManager manager = new TasksManager();

        //Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
        Task task1 = new Task("tasks.Task #1","tasks.Task #1 description","NEW");
        int task1Id = manager.addNewTask(task1);
        Task task1_test = manager.getTask(task1Id);
        assert (task1_test != task1);

        Task task2 = new Task("tasks.Task #2","tasks.Task #2 description","IN_PROGRESS");
        int task2Id = manager.addNewTask(task2);

        Epic epic1 = new Epic("tasks.Epic #1","tasks.Epic #1 description", "NEW");
        Epic epic2 = new Epic("tasks.Epic #2","tasks.Epic #2 description", "NEW");
        int epic1Id = manager.addNewEpic(epic1);
        int epic2Id = manager.addNewEpic(epic2);

        SubTask subTask1 = new SubTask("tasks.Epic #1 subTask #1","tasks.Epic #1 subTask #1 description","NEW", epic1Id);
        SubTask subTask2 = new SubTask("tasks.Epic #1 subTask #2","tasks.Epic #1 subTask #2 description","NEW", epic1Id);
        SubTask subTask3 = new SubTask("tasks.Epic #2 subTask #1","tasks.Epic #2 subTask #1 description","IN_PROGRESS", epic2Id);
        int subTask1Id = manager.addNewSubTask(subTask1);
        int subTask2Id = manager.addNewSubTask(subTask2);
        int subTask3Id = manager.addNewSubTask(subTask3);

        //Распечатайте списки эпиков, задач и подзадач
        System.out.println("Список всех задач, эпиков и сабтасков:");
        printAll(manager);
        System.out.println("");

        //Измените статусы созданных объектов, распечатайте. Проверьте, что статус задачи и подзадачи сохранился,
        //а статус эпика рассчитался по статусам подзадач.
        Task task_test = manager.getTask(task1Id);
        System.out.println("ТЕСТ: изменение статуса задачи");
        System.out.println("Статус задачи tasks.Task #1 до изменения: " + task_test.getStatus());
        task_test.setStatus("IN_PROGRESS");
        manager.updateTask(task_test);
        System.out.println("Изменили статус tasks.Task #1 NEW -> IN_PROGRESS..");
        System.out.println("Статус задачи tasks.Task #1 после изменения: " + task_test.getStatus() + "\n");

        System.out.println("ТЕСТ: удаление задачи tasks.Task #1");
        System.out.println("Перечень задач до удаления:");
        printAllTasks(manager);
        manager.deleteTask(task_test.getId());
        System.out.println("Перечень задач после удаления:");
        printAllTasks(manager);

        System.out.println("ТЕСТ: изменение статуса подзадачи");
        SubTask subTask_test = manager.getSubTask(subTask2Id);
        Epic epic_test = manager.getEpic(epic1Id);
        System.out.println("Статус эпика tasks.Epic #1: " + epic_test.getStatus());
        printEpicSubTasksStatus(manager, epic_test);
        subTask_test.setStatus("DONE");
        manager.updateSubTask(subTask_test);
        System.out.println("Изменили статус подзадачи tasks.Epic #1 subTask #2 NEW -> DONE");
        System.out.println("Статус эпика tasks.Epic #1: " + epic_test.getStatus());
        printEpicSubTasksStatus(manager, epic_test);
        System.out.println("");

        System.out.println("ТЕСТ: изменение статуса эпика");
        subTask_test = manager.getSubTask(subTask1Id);
        System.out.println("Статус эпика tasks.Epic #1: " + epic_test.getStatus());
        printEpicSubTasksStatus(manager, epic_test);
        subTask_test.setStatus("DONE");
        manager.updateSubTask(subTask_test);
        System.out.println("Изменился статус подзадачи tasks.Epic #1 subTask #1 NEW -> DONE");
        System.out.println("Статус эпика tasks.Epic #1: " + epic_test.getStatus());
        printEpicSubTasksStatus(manager, epic_test);
        System.out.println("");

        //удаляем одну из задач и один из эпиков
        System.out.println("ТЕСТ: удаление эпика");
        System.out.println("Список эпиков до удаления:");
        printAllEpics(manager);
        epic_test = manager.getEpic(epic2Id);
        manager.deleteEpic(epic_test.getId());
        System.out.println("Список эпиков после удаления tasks.Epic #2:");
        printAllEpics(manager);
        System.out.println("");

    }

    static void printAll(TasksManager manager) {
        System.out.println("Список всех задач:");
        printAllTasks(manager);
        System.out.println("Список всех подзадач:");
        printAllSubTasks(manager);
        System.out.println("Список всех эпиков:");
        printAllEpics(manager);
    }
    static void printAllTasks(TasksManager manager) {
        for (Task t : manager.getAllTasks()) {
            System.out.println(t);
        }
    }

    static void printAllSubTasks(TasksManager manager) {
        for (SubTask st : manager.getAllSubTasks()) {
            System.out.println(st);
        }
    }

    static void printAllEpics(TasksManager manager) {
        for (Epic e : manager.getAllEpics()) {
            System.out.println(e);
        }
    }

    static void printEpicSubTasksStatus(TasksManager manager, Epic epic) {
        for (SubTask st : manager.getEpicSubTasks(epic.getId())) {
            System.out.println("Статус подзадачи " + st.getName() + ": " + st.getStatus());
        }
    }
}
