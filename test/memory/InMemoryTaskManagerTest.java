package memory;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import tasks.TaskManagerTest;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

}
