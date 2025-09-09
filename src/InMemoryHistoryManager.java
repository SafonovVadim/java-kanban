import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.remove(0);
            history.add(task);
        } else history.add(task);

    }

    @Override
    public ArrayList<Task> getHistory() {
        if (history.isEmpty()) {
            return null;
        } else {
            return history;
        }
    }
}
