package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Потокобезопасный кольцевой буфер с ограниченной ёмкостью.
 * Старые записи автоматически вытесняются при переполнении.
 */
public class LogBuffer {
    private final int capacity;
    private final LogEntry[] buffer;
    private int head;       // индекс самой старой записи
    private int size;       // текущее количество записей
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LogBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.buffer = new LogEntry[capacity];
    }

    /**
     * Добавляет запись. При переполнении самая старая запись перезаписывается.
     */
    public void add(LogEntry entry) {
        lock.writeLock().lock();
        try {
            if (size < capacity) {
                buffer[(head + size) % capacity] = entry;
                size++;
            } else {
                buffer[head] = entry;
                head = (head + 1) % capacity;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Возвращает текущее количество записей.
     */
    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Возвращает снимок всех записей в порядке от старых к новым.
     */
    public List<LogEntry> getAll() {
        lock.readLock().lock();
        try {
            List<LogEntry> snapshot = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                snapshot.add(buffer[(head + i) % capacity]);
            }
            return Collections.unmodifiableList(snapshot);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Возвращает снимок подсписка записей от startIndex (включительно) до endIndex (исключительно).
     * Индексы соответствуют позициям в порядке от старых к новым.
     */
    public List<LogEntry> getRange(int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex > size || startIndex > endIndex) {
            throw new IndexOutOfBoundsException("Invalid range: " + startIndex + ".." + endIndex);
        }
        lock.readLock().lock();
        try {
            List<LogEntry> range = new ArrayList<>(endIndex - startIndex);
            for (int i = startIndex; i < endIndex; i++) {
                range.add(buffer[(head + i) % capacity]);
            }
            return Collections.unmodifiableList(range);
        } finally {
            lock.readLock().unlock();
        }
    }
}
