import interfaces.TaskManager;

abstract class TaskManagerTest<T extends TaskManager> {

    protected abstract T createTaskManager();

}
