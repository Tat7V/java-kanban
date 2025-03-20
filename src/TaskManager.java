import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int idNext = 1;

    //d. Создание.
    public void createTask (Task task) {
        task.setId(idNext++);
        tasks.put(task.getId(), task);

    }
    public void createEpic(Epic epic) {
        epic.setId(idNext++);
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(idNext++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
        }
    }

   //a. Получение списка всех задач.
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (Integer id : tasks.keySet()) {
            taskList.add(tasks.get(id));
        }
        return taskList;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            epicList.add(epics.get(id));
        }
        return epicList;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            subtaskList.add(subtasks.get(id));
        }
        return subtaskList;
    }

    //b. Удаление всех задач.
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : getAllEpics()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        }
    }

    // c. Получение по идентификатору.
    public Task getTaskById(int id) {
        for (Integer taskId : tasks.keySet()) {
            if (taskId.equals(id)) {
                return tasks.get(taskId);
            }
        }
        return null;
    }


    public Epic getEpicById(int id) {
        for (Integer epicId : epics.keySet()) {
            if (epicId.equals(id)) {
                return epics.get(epicId);
            }
        }
        return null;
    }

    public Subtask getSubtaskById(int id) {
        for (Integer subtaskId : subtasks.keySet()) {
            if (subtaskId.equals(id)) {
                return subtasks.get(subtaskId);
            }
        }
        return null;
    }

    //e. Обновление.
    public void updateTask(Task task) {
        for (Integer taskId : tasks.keySet()) {
            if (taskId.equals(task.getId())) {
                tasks.put(taskId, task);
                return;
            }
        }
    }

    public void updateEpic(Epic epic) {
        for (Integer epicId : epics.keySet()) {
            if (epicId.equals(epic.getId())) { 
                epics.put(epicId, epic);
                updateEpicStatus(epicId);
                return;
            }
        }
    }

    public void updateSubtask(Subtask subtask) {
        for (Integer subtaskId : subtasks.keySet()) {
            if (subtaskId.equals(subtask.getId())) { 
                subtasks.put(subtaskId, subtask);
                updateEpicStatus(subtask.getEpicId());
                return;
            }
        }
    }


        //f. Удаление по идентификатору.
    public void deleteTaskById(int id) {
        for (Integer taskId : tasks.keySet()) {
            if (taskId.equals(id)) {
                tasks.remove(taskId);
                return;
            }
        }
    }


    public void deleteEpicById(int id) {
        for (Integer epicId : epics.keySet()) {
            if (epicId.equals(id)) { // Проверка через equals
                Epic epic = epics.remove(epicId);
                for (int subtaskId : epic.getSubtaskIds()) {
                    subtasks.remove(subtaskId);
                }
                return;
            }
        }
    }
    
    
  
    public void deleteSubtaskById(int id){
        for (Integer subtaskId : subtasks.keySet()) {
            if (subtaskId.equals(id)) { 
                Subtask subtask = subtasks.remove(subtaskId);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.removeSubtaskId(subtaskId);
                    updateEpicStatus(epic.getId());
                }
                return;
            }
        }
    }



    //a.a Получение списка всех подзадач определённого эпика.
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> subtasksByEpicId = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    subtasksByEpicId.add(subtask);
                }
            }
        }
        return subtasksByEpicId;
    }

    //Статус ээпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            if (subtaskIds.isEmpty()) {
                epic.setStatus(Status.NEW);
                return;
            }

            boolean allDone = true;
            boolean allNew = true;

            for (int subtaskId : subtaskIds) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    if (subtask.getStatus() != Status.DONE) {
                        allDone = false;
                    }
                    if (subtask.getStatus() != Status.NEW) {
                        allNew = false;
                    }
                }
            }

            if (allDone) {
                epic.setStatus(Status.DONE);
            } else if (allNew) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}

















