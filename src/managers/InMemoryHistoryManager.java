package managers;

import entities.Task;
import interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.remove(0);
            history.add(task);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
