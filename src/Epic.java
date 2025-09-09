import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public void addSubtaskId(Task subtask) {
        if (subtask instanceof SubTask) {
            subtaskIds.add(subtask.getId());
        }
    }


    @Override
    public String toString() {
        return "Epic{" +
                " subtasks.size = " + subtaskIds.size() +
                ", subtasks.ids = " + subtaskIds +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }
}
