public class Main {
    public static void main(String[] args) {
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
    }
}