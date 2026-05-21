package collections;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class BoundedCircularList<T> implements List<T> {
    private final int capacity;
    private final Object[] buffer;
    private int head;
    private int size;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BoundedCircularList(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }


    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        lock.readLock().lock();
        try {
            for (int i = 0; i < size; i++) {
                if (Objects.equals(buffer[(head + i) % capacity], o)) return true;
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return snapshot().iterator();
    }

    @Override
    public Object[] toArray() {
        return snapshot().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return snapshot().toArray(a);
    }

    @Override
    public boolean add(T t) {
        lock.writeLock().lock();
        try {
            if (size < capacity) {
                buffer[(head + size) % capacity] = t;
                size++;
            } else {
                buffer[head] = t;
                head = (head + 1) % capacity;
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Remove not supported");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        lock.readLock().lock();
        try {
            for (Object o : c) {
                if (!contains(o)) return false;
            }
            return true;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        lock.writeLock().lock();
        try {
            for (T t : c) add(t);
            return !c.isEmpty();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("addAll at index not supported");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll not supported");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll not supported");
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            Arrays.fill(buffer, null);
            head = 0;
            size = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        lock.readLock().lock();
        try {
            return (T) buffer[(head + index) % capacity];
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("set not supported");
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("add at index not supported");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("remove by index not supported");
    }

    @Override
    public int indexOf(Object o) {
        lock.readLock().lock();
        try {
            for (int i = 0; i < size; i++) {
                if (Objects.equals(buffer[(head + i) % capacity], o)) return i;
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        lock.readLock().lock();
        try {
            for (int i = size - 1; i >= 0; i--) {
                if (Objects.equals(buffer[(head + i) % capacity], o)) return i;
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ListIterator<T> listIterator() {
        return snapshot().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return snapshot().listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        lock.readLock().lock();
        try {
            if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) throw new IndexOutOfBoundsException();
            List<T> result = new ArrayList<>(toIndex - fromIndex);
            for (int i = fromIndex; i < toIndex; i++) {
                result.add((T) buffer[(head + i) % capacity]);
            }
            return Collections.unmodifiableList(result);
        } finally {
            lock.readLock().unlock();
        }
    }


    public List<T> snapshot() {
        lock.readLock().lock();
        try {
            List<T> snapshot = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                snapshot.add((T) buffer[(head + i) % capacity]);
            }
            return snapshot;
        } finally {
            lock.readLock().unlock();
        }
    }
}
