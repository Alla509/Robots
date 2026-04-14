package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import log.Logger;

/**
 * Управление сохранением и загрузкой конфигурации приложения.
 * Файл хранится в домашнем каталоге пользователя.
 */
public class ConfigManager {
    private static final String CONFIG_FILE = System.getProperty("user.home") + File.separator + ".robot-config.properties";
    private final Properties props = new Properties();

    public void load() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return;
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (IOException e) {
            Logger.debug("Не удалось загрузить конфигурацию: " + e.getMessage());
        }
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Robot program configuration");
        } catch (IOException e) {
            Logger.debug("Не удалось сохранить конфигурацию: " + e.getMessage());
        }
    }

    public void setMainWindowBounds(int x, int y, int width, int height, int state) {
        setProperty("main.x", x);
        setProperty("main.y", y);
        setProperty("main.width", width);
        setProperty("main.height", height);
        setProperty("main.state", state);
    }

    public int getMainWindowX(int defaultValue) { return getIntProperty("main.x", defaultValue); }
    public int getMainWindowY(int defaultValue) { return getIntProperty("main.y", defaultValue); }
    public int getMainWindowWidth(int defaultValue) { return getIntProperty("main.width", defaultValue); }
    public int getMainWindowHeight(int defaultValue) { return getIntProperty("main.height", defaultValue); }
    public int getMainWindowState(int defaultValue) { return getIntProperty("main.state", defaultValue); }

    public void setWindowBounds(String windowName, int x, int y, int width, int height, boolean icon, boolean maximized) {
        setProperty(key(windowName, "x"), x);
        setProperty(key(windowName, "y"), y);
        setProperty(key(windowName, "width"), width);
        setProperty(key(windowName, "height"), height);
        setProperty(key(windowName, "icon"), icon);
        setProperty(key(windowName, "maximized"), maximized);
    }

    public boolean hasWindow(String windowName) {
        return props.containsKey(key(windowName, "x"));
    }

    public int getWindowX(String windowName, int defaultValue) { return getIntProperty(key(windowName, "x"), defaultValue); }
    public int getWindowY(String windowName, int defaultValue) { return getIntProperty(key(windowName, "y"), defaultValue); }
    public int getWindowWidth(String windowName, int defaultValue) { return getIntProperty(key(windowName, "width"), defaultValue); }
    public int getWindowHeight(String windowName, int defaultValue) { return getIntProperty(key(windowName, "height"), defaultValue); }
    public boolean getWindowIcon(String windowName, boolean defaultValue) { return getBooleanProperty(key(windowName, "icon"), defaultValue); }
    public boolean getWindowMaximized(String windowName, boolean defaultValue) { return getBooleanProperty(key(windowName, "maximized"), defaultValue); }

    private String key(String windowName, String suffix) {
        return windowName + "." + suffix;   // единственное место с конкатенацией (инкапсулировано)
    }

    private void setProperty(String key, int value) {
        props.setProperty(key, Integer.toString(value));
    }

    private void setProperty(String key, boolean value) {
        props.setProperty(key, Boolean.toString(value));
    }

    private int getIntProperty(String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = props.getProperty(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }
}
