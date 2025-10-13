package managers;


import entities.*;
import exceptions.ManagerSaveException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File filePath;

    public FileBackedTaskManager(File file) {
        this.filePath = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null || !file.exists()) {
            throw new ManagerSaveException("Файл не существует: " + file);
        }

        try {
            FileBackedTaskManager manager = new FileBackedTaskManager(file);
            manager.load(file);
            return manager;
        } catch (Exception e) {
            throw new ManagerSaveException("Не удалось загрузить менеджер из файла: " + file.getAbsolutePath());
        }
    }


    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath.toPath())) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            for (Task task : getAllTasks()) {
                writer.write(task.toString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString(epic));
                writer.newLine();
            }
            for (SubTask subtask : getAllSubtasks()) {
                writer.write(subtask.toString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + filePath);
        }
    }

    private void load(File filePath) {
        try (BufferedReader reader = Files.newBufferedReader(filePath.toPath())) {
            List<String> lines = reader.lines().toList();
            for (String line : lines) {
                if (line.isEmpty() || line.startsWith("id")) {
                    continue;
                }
                Task task = fromString(line);
                if (task instanceof SubTask) {
                    createSubtask((SubTask) task);
                } else if (task instanceof Epic) {
                    createEpic((Epic) task);
                } else {
                    createTask(task);
                }
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка парсинга файла: " + filePath);
        }
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        Type type = Type.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description, status);
            case SUBTASK:
                int epicId = Integer.parseInt(parts.length > 5 ? parts[5] : "0");
                return new SubTask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void createSubtask(SubTask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
