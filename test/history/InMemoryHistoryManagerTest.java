package history;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.NEW;

public class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void getEmptyHistoryTest() {
        //Пустая история задач
        assertEquals(0,historyManager.getHistory().size(),"История не пустая");
    }

    @Test
    void getHistoryTest() {
        Task task = new Task("Task #1","Task #1 description", NEW);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size(),"История пустая");
        assertEquals(task, historyManager.getHistory().get(0),"Задачи не совпадают");
    }

    @Test
    void duplicateHistoryTest() {
        //Дублирование
        Task task = new Task("Task #1","Task #1 description", NEW);
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1,historyManager.getHistory().size(),"В истории есть дубликаты");
    }

    @Test
    void addTest() {
        Task task = new Task("Task #1","Task #1 description", NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(task, history.get(0),"Задача не добавлена в историю.");
    }

    @Test
    void removeFromBeginningTest() {
    //Удаление из истории: в начале
        Task task1 = new Task("Task #1","Task #1 description", NEW);
        Task task2 = new Task("Task #2","Task #2 description", NEW);
        Task task3 = new Task("Task #3","Task #3 description", NEW);
        task1.setId(0);
        task2.setId(1);
        task3.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(3, historyManager.getHistory().size(),"Количество задач в истории не совпадает");
        assertEquals(task1, historyManager.getHistory().get(0),"Задача в истории не совпадает");

        historyManager.remove(task1.getId());
        assertEquals(2, historyManager.getHistory().size(),"Количество задач в истории не совпадает");
        assertEquals(task2, historyManager.getHistory().get(0),"Задача из начала истории не удалена");
    }

    @Test
    void removeFromMiddleTest() {
        //Удаление из истории: в середине
        Task task1 = new Task("Task #1","Task #1 description", NEW);
        Task task2 = new Task("Task #2","Task #2 description", NEW);
        Task task3 = new Task("Task #3","Task #3 description", NEW);
        task1.setId(0);
        task2.setId(1);
        task3.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(task2, historyManager.getHistory().get(1),"Задача в истории не совпадает");
        assertEquals(3, historyManager.getHistory().size(),"Количество задач в истории не совпадает");

        historyManager.remove(task2.getId());
        assertEquals(task3, historyManager.getHistory().get(1),"Задача из середины истории не удалена");
        assertEquals(2, historyManager.getHistory().size(),"Количество задач в истории не совпадает");
    }

    @Test
    void removeFromEndTest() {
        //Удаление из истории: в конце
        Task task1 = new Task("Task #1","Task #1 description", NEW);
        Task task2 = new Task("Task #2","Task #2 description", NEW);
        Task task3 = new Task("Task #3","Task #3 description", NEW);
        task1.setId(0);
        task2.setId(1);
        task3.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(task3, history.get(2),"Задача в истории не совпадает");
        assertEquals(3, history.size(),"Количество задач в истории не совпадает");

        historyManager.remove(task3.getId());
        assertEquals(2, historyManager.getHistory().size(),"Количество задач в истории не совпадает");
    }

}
