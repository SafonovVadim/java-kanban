package managers;

import entities.Task;
import interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.head = new Node(null);
        this.tail = new Node(null);
    }

    public Map<Integer, Node> getHistoryMap() {
        return historyMap;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
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

        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
        node.prev = null;
        node.next = null;

        historyMap.remove(node.task.getId());
    }

    public class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
            this.next = tail;
            this.prev = head;
        }

        public Task getTask() {
            return task;
        }

        public Node getPrev() {
            return prev;
        }

        public Node getNext() {
            return next;
        }
    }
}
