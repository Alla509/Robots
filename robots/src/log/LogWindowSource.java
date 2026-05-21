package log;

import collections.BoundedCircularList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogWindowSource {
    private final BoundedCircularList<LogEntry> buffer;
    private final List<LogChangeListener> listeners = new ArrayList<>();
    private volatile LogChangeListener[] activeListeners;

    public LogWindowSource(int capacity) {
        this.buffer = new BoundedCircularList<>(capacity);
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            activeListeners = null;
        }
    }


    public void append(LogLevel level, String message) {
        LogEntry entry = new LogEntry(level, message);
        buffer.add(entry);

        LogChangeListener[] copy = activeListeners;
        if (copy == null) {
            synchronized (listeners) {
                if (activeListeners == null) {
                    activeListeners = listeners.toArray(new LogChangeListener[0]);
                    copy = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : copy) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return buffer.size();
    }


    public Iterable<LogEntry> all() {
        return buffer.subList(0, buffer.size());
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        int total = buffer.size();
        if (startFrom < 0 || startFrom >= total) {
            return Collections.emptyList();
        }
        int end = Math.min(startFrom + count, total);
        return buffer.subList(startFrom, end);
    }
}
