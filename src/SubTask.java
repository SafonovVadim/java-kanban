import java.util.ArrayList;

public class SubTask extends Task {
    private final int epicId; //id вместо епика

    public SubTask(String title, String description, Status status, Epic epic) {
        super(title, description, status);
        this.epicId = epic.getId();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask: " + super.toString();
    }
}
