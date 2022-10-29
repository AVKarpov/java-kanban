package manager;

import tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = head;

        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    private void linkLast(Node node) {
        if (tail == null)
            head = node;
        else {
            node.prev = tail;
            tail.next = node;
        }
        tail = node;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        Node node = new Node(task, null, null);
        //remove from history
        remove(task.getId());
        //add as a last one
        linkLast(node);
        //add into the Map
        nodeMap.put(task.getId(),node);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        if (node != null)
            removeNode(node);
    }
    private void removeNode(Node node) {
        //remove from the Map
        nodeMap.remove(node.task.getId());

        //1. node is the head
        if (node.prev == null) {
            head = node.next;
            if (node.next != null)
                node.next.prev = null;
        }
        //2. node is the tail
        if (node.next == null) {
            tail = node.prev;
            if (node.prev != null)
                node.prev.next = null;
        }
        //3. node is in the middle
        if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }
}
