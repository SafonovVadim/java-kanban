package managers;

import entities.Task;
import interfaces.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public void add(Task task) {
        if (task == null || task.getId() == 0) return;

        Node newNode = new Node(task);
        historyMap.put(task.getId(), newNode);

        linkLast(newNode);
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

    private void linkLast(Node node) {
        if (head == null || tail == null) {
            head = new Node(null);
            tail = new Node(null);
            head.next = tail;
            tail.prev = head;
        }

        node.prev = tail.prev;
        node.next = tail;

        if (tail.prev != null) {
            tail.prev.next = node;
        }

        tail.prev = node;
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
        if (node == null) {
            return;
        }

        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode != null && nextNode != null) {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }

        node.prev = null;
        node.next = null;

        historyMap.remove(node.task.getId());
    }

    private class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }
}
