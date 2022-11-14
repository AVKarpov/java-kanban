package manager;

import java.io.IOException;

public class ManagerSaveException extends Throwable {
    public ManagerSaveException(IOException e) {
        System.out.println("Возникло исключение при работе с файлом в FileBackedTasksManager");
    }
}
