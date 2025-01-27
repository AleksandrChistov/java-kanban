package ru.yandex.practicum.task.managers;

import ru.yandex.practicum.task.interfaces.HistoryManager;
import ru.yandex.practicum.task.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first = null;
    private Node last = null;

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            Node node = nodeMap.get(task.getId());
            removeNode(node);
        }
        Node linkedNode = linkLast(task);
        nodeMap.put(task.getId(), linkedNode);
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            Node node = nodeMap.remove(id);
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        for (Node x = first; x != null; ) {
            tasks.add(x.task);
            x = x.next;
        }

        return tasks;
    }

    private Node linkLast(Task task) {
        final Node prevLast = last;
        final Node newNode = new Node(prevLast, task, null);
        last = newNode;

        if (prevLast == null) {
            first = newNode;
        } else {
            prevLast.next = newNode;
        }

        return newNode;
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.task = null;
    }

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return task.equals(node.task) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
        }

        @Override
        public int hashCode() {
            int result = task.hashCode();
            result = 31 * result + Objects.hashCode(next);
            result = 31 * result + Objects.hashCode(prev);
            return result;
        }
    }

}
