import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds;


    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int idSubtask) {
        subtaskIds.remove(idSubtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskId=" + subtaskIds +
                '}';
    }
}
