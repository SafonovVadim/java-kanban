package managers;

import entities.Task;
import interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private final Node head = new Node(null);
    private final Node tail = new Node(null);

    public InMemoryHistoryManager() {
        head.next = tail;
        tail.prev = head;
    }

    public Map<Integer, Node> getHistoryMap() {
        return historyMap;
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }


    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    public void linkLast(Task task) {
        if (task == null || task.getId() == 0) return;

        Node newNode = new Node(task);
        historyMap.put(task.getId(), newNode);

        newNode.prev = tail.prev;
        newNode.next = tail;
        tail.prev.next = newNode;
        tail.prev = newNode;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head.next;

        while (current != tail) {
            if (current.task != null) {
                tasks.add(current.task);
            }
            current = current.next;
        }

        return tasks;
    }

    public void removeNode(Node node) {
        if (node == null || node == head || node == tail) {
            return;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
        historyMap.remove(node.task.getId());
    }

    public static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }
}
