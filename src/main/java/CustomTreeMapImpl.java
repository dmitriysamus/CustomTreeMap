import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 *Map на основе бинарного дерева поиска. Реализует интерфейс {@link CustomTreeMap}.
 */

public class CustomTreeMapImpl <K, V> implements CustomTreeMap <K, V> {
    /**
     * Узел бинарного дерева.
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right;
        Node<K, V> parent;
        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
        void setLeftChild(Node<K, V> node) {
            this.left = node;
            if (this.left != null) {
                this.left.parent = this;
            }
        }
        void setRightChild(Node<K, V> node) {
            this.right = node;
            if (this.right != null) {
                this.right.parent = this;
            }
        }
    }

    private Node<K, V> root;
    private Comparator<K> comparator;
    private int size;

    /**
     * Конструктор объекта, принимающий на вход comparator.
     */
    public CustomTreeMapImpl(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    /**
     * Возвращает размер словаря.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Проверяет словарь на наличие объектов.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Возвращает значение по ключу.
     */
    @Override
    public Object get(Object key) {
        if (size == 0) {
            return null;
        }
        Node<K, V> result = recursiveSearch(root, (K) key);

        if (result == null) {
            return null;
        } else {
            return result.value;
        }
    }

    /**
     * Рекурсивно ищет объекта в дереве по значению.
     */
    private Node<K,V> recursiveSearch(Node<K,V> node, K key) {
        int compare = comparator.compare(key, node.key);
        if (compare == 0) {
            return node;
        }
        if (compare > 0) {
            if (node.right == null) {
                return null;
            } else {
                return recursiveSearch(node.right, key);
            }

        } else {
            if (node.left == null) {
                return null;
            } else {
                return recursiveSearch(node.left, key);
            }
        }
    }

    /**
     * Добавляет объект в словарь.
     */
    @Override
    public Object put(Object key, Object value) {
        if (root == null) {
            root = new Node<K, V>((K)key, (V)value);
            root.parent = null;
            size++;
            return null;
        }
        return append(root, (K)key, (V)value);
    }

    /**
     * Добавляет объект в словарь.
     */
    private Object append(Node<K,V> node, K key, V value) {
        int compare = comparator.compare(key, node.key);
        if (compare == 0) {
            V oldValue = node.value;
            node.value = value;
            return oldValue;
        }
        if (compare > 0) {
            if (node.right == null) {
                node.right = new Node<K, V>((K)key, (V)value);
                node.right.parent = node;
                size++;
                return null;
            } else {
                return append(node.right, key, value);
            }

        } else {
            if (node.left == null) {
                node.left = new Node<K, V>((K)key, (V)value);
                node.left.parent = node;
                size++;
                return null;
            } else {
                return append(node.left, key, value);
            }
        }
    }

    /**
     * Удаляет объект из словаря.
     */
    @Override
    public Object remove(Object key) {
        if (root == null) {
            return null;
        }

        if (root.key == key) {
            Node<K, V> result = root;
            removeThrough(root);
            return result.value;
        }

        Object result = get(key);
        if (!removeBefore(root, (K) key)) {
            result = null;
        }
        return result;
    }

    /**
     * Рекурсивно пробегается по дереву для удаления объекта и сдвига узлов.
     */
    private void removeThrough(Node<K,V> node) {
        if (node.right == null && node.left == null) {
            if (node.parent == null) {
                root = null;
                size = 0;
                return;
            }

            if (node.parent.left == node) {
                node.parent.left = null;
                size--;
                return;
            }
            if (node.parent.right == node) {
                node.parent.right = null;
                size--;
                return;
            }
        }

        if (node.right == null) {
            swap(node, node.left);
            return;
        }

        if (node.right != null) {
            if (node.right.left == null) {
                node.right.setLeftChild(node.left);
                swap(node, node.right);
                return;
            }

            if (node.right.left != null) {
                Node<K, V> tmpLeft = node.right;
                Node<K, V> tmpPrev = null;
                while (tmpLeft.left != null) {
                    tmpPrev = tmpLeft;
                    tmpLeft = tmpLeft.left;
                }

                if (tmpLeft.right != null) {
                    tmpPrev.left = tmpLeft.right;
                } else {
                    tmpPrev.left = null;
                }
                tmpLeft.setLeftChild(node.left);
                tmpLeft.setRightChild(node.right);
                swap(node, tmpLeft);
                return;
            }
        }
    }

    /**
     * Меняет узлы местами при удалении узла.
     */
    private void swap(Node<K, V> node, Node<K, V> changeNode) {
        if (node.parent == null) {
            changeNode.parent = null;
            root = changeNode;
        } else if (node.parent.left == node) {
            changeNode.parent = node.parent;
            node.parent.left = changeNode;
        } else if (node.parent.right == node) {
            changeNode.parent = node.parent;
            node.parent.right = changeNode;
        }
        size--;
    }

    /**
     * Удаляет объект из множества и упорядочивает словарь.
     */
    private boolean removeBefore(Node<K, V> node, K delKey) {
        int result = comparator.compare(delKey, node.key);
        if (result == 0) {
            removeThrough(node);
            return true;
        }
        if (result < 0) {
            if (node.left == null) {
                return false;
            } else {
                return removeBefore(node.left, delKey);
            }
        } else {
            if (node.right == null) {
                return false;
            } else {
                return removeBefore(node.right, delKey);
            }
        }
    }

    /**
     * Проверяет наличие ключа объекта в словаре.
     */
    @Override
    public boolean containsKey(Object key) {
        if (root == null) {
            return false;
        }

        Node<K, V> result = recursiveSearch(root, (K) key);

        if (result == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Проверяет наличия значения объекта в словаре.
     */
    @Override
    public boolean containsValue(Object value) {
        if (root == null) {
            return false;
        }

        Collection<Node<K, V>> array = new ArrayList<>(size);
        fillRecursive(root, array);

        for (Node<K, V> node : array) {
            if (Objects.equals(node.value, value)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Заполняет массив объектами из словаря.
     */
    private void fillRecursive(Node<K, V> node, Collection<Node<K, V>> array) {
        if (node.left != null) {
            fillRecursive(node.left, array);
        }
        array.add(node);
        if (node.right != null) {
            fillRecursive(node.right, array);
        }
    }

    /**
     * Возврящает множество всех ключей словаря.
     */
    @Override
    public Object[] keys() {
        Collection<Node<K, V>> array = new ArrayList<>(size);
        if (root != null) {
            fillRecursive(root, array);
        }

        Object[] key = new Object[size];
        int j = 0;
        for (Node<K, V> node : array) {
            key[j++] = node.key;
        }

        return key;
    }

    /**
     * Возврящает множество всех значений словаря.
     */
    @Override
    public Object[] values() {
        Collection<Node<K, V>> array = new ArrayList<>(size);

        if (root != null) {
            fillRecursive(root, array);
        }

        Object[] values = new Object[size];
        int j = 0;

        for (Node<K, V> node : array) {
            values[j++] = node.value;
        }

        return values;
    }

    /**
     * Возвращает строковое представление дерева.
     */
    @Override
    public String toString() {

        Collection<Node<K, V>> array = new ArrayList<>(size);

        if (root != null) {
            fillRecursive(root, array);
        }

        StringBuilder cb = new StringBuilder();

        cb.append("[ ");
        for (Node<K, V> node : array) {
            cb.append(" {key=" + node.key + ";value=" + node.value + "} ");
        }
        cb.append("]");
        return cb.toString();
    }
}
