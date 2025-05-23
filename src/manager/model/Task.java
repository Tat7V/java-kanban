package manager.model;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;


    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0) {
            return;
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public Status getStatus() {
        return status;
    }


    public void setStatus(Status status) {

        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id; // по условию, две задачи с одинаковым id считаются одной и той же задачей, даже если другие их поля различаются
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }


}
