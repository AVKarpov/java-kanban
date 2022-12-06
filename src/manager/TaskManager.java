package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    //Получение списка всех задач, подзадач, эпиков
    ArrayList<Task> getAllTasks();
    ArrayList<SubTask> getAllSubTasks();
    ArrayList<Epic> getAllEpics();

    //Получение по идентификатору
    Task getTask(int id);
    SubTask getSubTask(int id);
    Epic getEpic(int id);
    ArrayList<SubTask> getEpicSubTasks(int epicId);

    Set<Task> getPrioritizedTasks();

    //Создание. Сам объект должен передаваться в качестве параметра
    int addNewTask(Task task);
    int addNewSubTask(SubTask subTask);
    int addNewEpic(Epic epic);

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра
    void updateTask(Task task);
    void updateSubTask(SubTask subTask);
    void updateEpic(Epic epic);

    //Удаление всех задач
    void deleteAllTasks();
    //Удаление по идентификатору
    void deleteTask(int id);
    void deleteSubTask(int id);
    void deleteEpic(int id);

    List<Task> getHistory();
}
