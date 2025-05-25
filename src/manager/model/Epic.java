package manager.model;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds;
    private LocalDateTime endTime;


    public Epic(int id, String name, String description, Status status)  {
        super(id, name, description, status, null, null);
        this.subtaskIds = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.getId()) {
            subtaskIds.add(subtaskId);
        }
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
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
