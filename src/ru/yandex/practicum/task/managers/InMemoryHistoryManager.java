package ru.yandex.practicum.task.managers;

import ru.yandex.practicum.task.interfaces.HistoryManager;
import ru.yandex.practicum.task.tasks.Task;

import java.util.*;

/**
 * Реализация интерфейса {@link HistoryManager} для хранения истории просмотров задач в памяти.
 * <p>
 * Использует двусвязный список для хранения задач в порядке их просмотра и HashMap для быстрого доступа к узлам списка.
 * Добавление и удаление задач работает за O(1).
 */
public class InMemoryHistoryManager implements HistoryManager {
    /**
     * Карта для быстрого доступа к узлам списка по идентификатору задачи.
     */
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first = null;
    private Node last = null;

    /**
     * Добавляет задачу в историю просмотра.
     * <p>
     * Если задача с таким идентификатором уже есть в истории, она удаляется из текущей позиции и добавляется в конец списка,
     * таким образом, обновляя порядок просмотра.
     * @param task Задача, которую необходимо добавить в историю.
     */
    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            Node node = nodeMap.get(task.getId());
            removeNode(node);
        }
        Node linkedNode = linkLast(task);
        nodeMap.put(task.getId(), linkedNode);
    }

    /**
     * Удаляет задачу из истории просмотра по ее идентификатору.
     * @param id Идентификатор задачи, которую необходимо удалить из истории.
     */
    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            Node node = nodeMap.remove(id);
            removeNode(node);
        }
    }

    /**
     * Возвращает историю просмотра задач в виде списка.
     * @return Список задач в порядке их просмотра, от самой старой к самой новой.
     */
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    /**
     * Вспомогательный метод для получения списка задач из двусвязного списка.
     * @return Список задач в порядке их просмотра, от самой старой к самой новой.
     */
    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        for (Node x = first; x != null; ) {
            tasks.add(x.task);
            x = x.next;
        }

        return tasks;
    }

    /**
     * Вспомогательный метод для добавления задачи в конец двусвязного списка.
     * @param task Задача, которую необходимо добавить в конец списка.
     * @return Новый узел двусвязного списка, представляющий добавленную задачу.
     */
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

    /**
     * Вспомогательный метод для удаления узла из двусвязного списка.
     * @param node Узел двусвязного списка, который необходимо удалить.
     */
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

    /**
     * Внутренний статический класс, представляющий узел двусвязного списка.
     */
    private static class Node {
        Task task;
        Node next;
        Node prev;

        /**
         * @param prev Ссылка на предыдущий узел.
         * @param task Задача, хранящаяся в узле.
         * @param next Ссылка на следующий узел.
         */
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
