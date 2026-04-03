package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private static final String CONFIG_FILE = System.getProperty("user.home") + File.separator + ".robot-config.properties";

    public MainApplicationFrame() {
        loadWindowState();
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        loadInternalWindowsState();

        MenuBarFactory menuFactory = new MenuBarFactory(this);
        setJMenuBar(menuFactory.createMenuBar());

        //закрытие приложения через крестик
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    public void exitApplication() {
        //так как в UIManager уже установлен русский язык, то не нужно делать это дополнительно
        saveWindowState();
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти из приложения?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Метод для смены Look&Feel (публичный, чтобы был доступен из MenuBarFactory)
     */
    public void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            RobotsProgram.setRussianUIManagerText();
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    /**
     * Загружает состояние главного окна из файла конфигурации.
     * Если файл отсутствует, устанавливает размеры по умолчанию.
     */
    private void loadWindowState() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            // геометрия по умолчанию (как было в конструкторе)
            int inset = 50;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(inset, inset,
                    screenSize.width - inset * 2,
                    screenSize.height - inset * 2);
            return;
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);

            int x = Integer.parseInt(props.getProperty("main.x", "50"));
            int y = Integer.parseInt(props.getProperty("main.y", "50"));
            int width = Integer.parseInt(props.getProperty("main.width", "800"));
            int height = Integer.parseInt(props.getProperty("main.height", "600"));
            int state = Integer.parseInt(props.getProperty("main.state", String.valueOf(JFrame.NORMAL)));

            setBounds(x, y, width, height);
            setExtendedState(state);
        } catch (IOException | NumberFormatException e) {
            Logger.debug("Не удалось загрузить конфигурацию: " + e.getMessage());
            // в случае ошибки используем геометрию по умолчанию
            int inset = 50;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(inset, inset,
                    screenSize.width - inset * 2,
                    screenSize.height - inset * 2);
        }
    }

    /*
     * Сохраняет состояние главного окна и всех внутренних окон в файл.
        */
    private void saveWindowState() {
        Properties props = new Properties();

        // Главное окно
        props.setProperty("main.x", String.valueOf(getX()));
        props.setProperty("main.y", String.valueOf(getY()));
        props.setProperty("main.width", String.valueOf(getWidth()));
        props.setProperty("main.height", String.valueOf(getHeight()));
        props.setProperty("main.state", String.valueOf(getExtendedState()));

        // Внутренние окна
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            JInternalFrame f = frames[i];
            String title = f.getTitle();
            if (title == null || title.isEmpty()) continue;

            String prefix = "window." + i + ".";
            props.setProperty(prefix + "title", title);
            props.setProperty(prefix + "x", String.valueOf(f.getX()));
            props.setProperty(prefix + "y", String.valueOf(f.getY()));
            props.setProperty(prefix + "width", String.valueOf(f.getWidth()));
            props.setProperty(prefix + "height", String.valueOf(f.getHeight()));
            props.setProperty(prefix + "icon", String.valueOf(f.isIcon()));
            props.setProperty(prefix + "maximized", String.valueOf(f.isMaximum()));
        }

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Robot program configuration");
        } catch (IOException e) {
            Logger.debug("Не удалось сохранить конфигурацию: " + e.getMessage());
        }
    }

    /**
     * Восстанавливает состояние внутренних окон после их создания.
     */
    private void loadInternalWindowsState() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) return;

        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);

            JInternalFrame[] frames = desktopPane.getAllFrames();
            for (JInternalFrame frame : frames) {
                String title = frame.getTitle();
                if (title == null) continue;

                // Ищем запись с таким же заголовком
                for (String key : props.stringPropertyNames()) {
                    if (key.endsWith(".title") && props.getProperty(key).equals(title)) {
                        String prefix = key.substring(0, key.length() - 6); // убираем ".title"
                        try {
                            int x = Integer.parseInt(props.getProperty(prefix + ".x", "0"));
                            int y = Integer.parseInt(props.getProperty(prefix + ".y", "0"));
                            int w = Integer.parseInt(props.getProperty(prefix + ".width", "300"));
                            int h = Integer.parseInt(props.getProperty(prefix + ".height", "200"));
                            boolean icon = Boolean.parseBoolean(props.getProperty(prefix + ".icon", "false"));
                            boolean maximized = Boolean.parseBoolean(props.getProperty(prefix + ".maximized", "false"));

                            frame.setBounds(x, y, w, h);
                            try {
                                if (maximized) {
                                    frame.setMaximum(true);
                                } else if (icon) {
                                    frame.setIcon(true);
                                }
                            } catch (java.beans.PropertyVetoException e) {
                                Logger.debug("Не удалось установить состояние окна " + title + ": " + e.getMessage());
                            }
                        } catch (NumberFormatException e) {
                            Logger.debug("Ошибка парсинга конфигурации для окна " + title);
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            Logger.debug("Не удалось загрузить конфигурацию внутренних окон: " + e.getMessage());
        }
    }
}
