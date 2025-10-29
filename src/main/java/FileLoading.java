import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import managers.FileBackedTaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public static void main(String[] args) {
    // Сценарий по сохранению в файл
    System.out.println("\n=== Сохранение в файл ===");
    File filePath = new File("tasks.csv");

    FileBackedTaskManager managerFile = new FileBackedTaskManager(filePath);

    Task task = new Task("Задача", "Описание", Status.NEW);
    task.setId(1);
    managerFile.createTask(task);

    Epic epicFile = new Epic("Эпик", "Описание эпика", Status.NEW);
    epicFile.setId(2);
    managerFile.createEpic(epicFile);

    SubTask subtask = new SubTask("Подзадача", "Описание подзадачи", Status.DONE, epicFile);
    subtask.setId(3);
    managerFile.createSubtask(subtask);

    Task task2 = new Task(4, "Задача", "Описание", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
    managerFile.createTask(task2);

    Epic epicFile1 = new Epic(5, "Эпик", "Описание эпика", Status.NEW,  LocalDateTime.now().plus(1, ChronoUnit.HOURS));
    managerFile.createEpic(epicFile1);

    SubTask subtask3 = new SubTask(6, "Подзадача", "Описание подзадачи", Status.DONE, Duration.ofMinutes(10), LocalDateTime.now().plus(2, ChronoUnit.HOURS), epicFile1.getId());
    managerFile.createSubtask(subtask3);
    SubTask subtask4 = new SubTask(7, "Подзадача", "Описание подзадачи", Status.DONE, Duration.ofMinutes(10), LocalDateTime.now().plus(6, ChronoUnit.HOURS), epicFile1.getId());
    managerFile.createSubtask(subtask4);
    SubTask subtask5 = new SubTask(8, "Подзадача", "Описание подзадачи", Status.DONE, Duration.ofMinutes(10), LocalDateTime.now().plus(4, ChronoUnit.HOURS), epicFile1.getId());
    managerFile.createSubtask(subtask5);
    managerFile.printAllTasks();
    epicFile1.updateFromSubtasks(List.of(subtask3));
    System.out.println("\nПриоритизированные задачи:");
    System.out.println(managerFile.getPrioritizedTasks());
    System.out.println("Данные сохранены в: " + filePath);
    FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(filePath);

    System.out.println("\nЗагруженные задачи:");
    for (Task t : loadedManager.getAllTasks()) {
        System.out.println(t);
    }

    System.out.println("\nЗагруженные эпики:");
    for (Epic e : loadedManager.getAllEpics()) {
        System.out.println(e);
    }

    System.out.println("\nЗагруженные подзадачи:");
    for (SubTask s : loadedManager.getAllSubtasks()) {
        System.out.println(s);
    }
}
