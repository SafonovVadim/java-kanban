import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import managers.Managers;

public class Main {
    public static void main(String[] args) throws ManagerSaveException {
        //Основной сценарий
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Закупить продукты", "Купить хлеб, молоко и яйца", Status.NEW);
        manager.createTask(task1);

        Epic epic = new Epic("Переезд", "Планируем переезд в новую квартиру", Status.NEW);
        manager.createEpic(epic);

        SubTask subtask1 = new SubTask("Упаковать вещи", "Собрать коробки", Status.NEW, epic);
        SubTask subtask2 = new SubTask("Заказать машину", "Нанять грузчики", Status.NEW, epic);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.printAllTasks();
        manager.updateEpic(epic);
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateEpic(epic);
        manager.printAllTasks();
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);
        manager.updateEpic(epic);
        manager.printAllTasks();
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic.getId());
        manager.deleteSubtask(subtask1.getId());
        manager.printAllTasks();

        //Дополнительный сценарий
        // 1. Инициализация менеджера задач и истории
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        System.out.println("=== Второй сценарий ===");
        // 2. Создание задач
        Task task11 = new Task("Задача 1", "Описание");
        Task task22 = new Task("Задача 2", "Другое описание");

        taskManager.createTask(task11);
        taskManager.createTask(task22);

        // 3. Создание эпика без подзадач
        Epic epic1 = new Epic("Эпик 1", "Без подзадач", Status.NEW);
        taskManager.createEpic(epic1);

        // 4. Создание эпика с тремя подзадачами
        Epic epic2 = new Epic("Эпик 2", "С подзадачами", Status.NEW);
        taskManager.createEpic(epic2);

        SubTask subtask11 = new SubTask("Подзадача 1", "Описание", Status.NEW, epic2);
        SubTask subtask22 = new SubTask("Подзадача 2", "Описание", Status.NEW, epic2);
        SubTask subtask33 = new SubTask("Подзадача 3", "Описание", Status.NEW, epic2);

        taskManager.createSubtask(subtask11);
        taskManager.createSubtask(subtask22);
        taskManager.createSubtask(subtask33);

        // 5. Запросы задач в разных порядках
        System.out.println("=== Порядок запросов ===");
        System.out.println("Запрашиваем: эпик 2, задача 1, подзадача 2, задача 2, подзадача 1");

        taskManager.getTaskById(epic2.getId());
        taskManager.getTaskById(task11.getId());
        taskManager.getTaskById(subtask22.getId());
        taskManager.getTaskById(task22.getId());
        taskManager.getTaskById(subtask11.getId());

        System.out.println("\n=== История после первых запросов ===");
        taskManager.printAllTasks();

        // 6. Добавляем ещё один запрос (дублирующий)
        taskManager.getTaskById(task11.getId());
        System.out.println("\n=== История после повторного запроса задачи 1 ===");
        taskManager.printAllTasks();

        // 7. Удаляем задачу 1 из истории
        historyManager.remove(task11.getId());
        System.out.println("\n=== История после удаления задачи 1 ===");
        taskManager.printAllTasks();

        // 8. Удаляем эпик 2 — должны удалиться и все его подзадачи
        taskManager.deleteEpic(epic2.getId());
        System.out.println("\n=== История после удаления эпика 2 и его подзадач ===");
        taskManager.printAllTasks();
    }
}
