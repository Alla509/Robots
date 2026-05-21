package log;

import collections.BoundedCircularList;
import java.util.List;

public class LogBuffer {
    private final BoundedCircularList<LogEntry> buffer;

    public LogBuffer(int capacity) {
        buffer = new BoundedCircularList<>(capacity);
    }

    public void add(LogEntry entry) {
        buffer.add(entry);
    }

    public int size() {
        return buffer.size();
    }

    public List<LogEntry> getAll() {
        return buffer.snapshot();
    }

    // Для совместимости
    public List<LogEntry> getRange(int start, int end) {
        return buffer.subList(start, end);
    }
}
