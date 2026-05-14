package log;

import java.util.ArrayList;
import java.util.Collections;

public class LogWindowSource {
    private final LogBuffer logBuffer;          // ограниченная очередь
    private final ArrayList<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength) {
        logBuffer = new LogBuffer(iQueueLength);
        m_listeners = new ArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        logBuffer.add(entry);

        LogChangeListener[] activeListeners = m_activeListeners;
        if (activeListeners == null) {
            synchronized (m_listeners) {
                if (m_activeListeners == null) {
                    activeListeners = m_listeners.toArray(new LogChangeListener[0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return logBuffer.size();
    }


    public Iterable<LogEntry> range(int startFrom, int count) {
        int total = size();
        if (startFrom < 0 || startFrom >= total) {
            return Collections.emptyList();
        }
        int endIndex = Math.min(startFrom + count, total);
        return logBuffer.getRange(startFrom, endIndex);
    }


    public Iterable<LogEntry> all() {
        return logBuffer.getAll();
    }
}
