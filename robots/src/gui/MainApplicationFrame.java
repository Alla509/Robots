package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import config.ConfigManager;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private static final String LOG_WINDOW_TITLE = "Протокол работы";
    private static final String GAME_WINDOW_TITLE = "Игровое поле";
    private static final String LOG_WINDOW_NAME = "LogWindow";
    private static final String GAME_WINDOW_NAME = "GameWindow";

    private final JDesktopPane desktopPane = new JDesktopPane();
    private final ConfigManager configManager = new ConfigManager();

    public MainApplicationFrame() {
        configManager.load();
        loadMainWindowState();

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        loadInternalWindowState(LOG_WINDOW_NAME, logWindow);
        loadInternalWindowState(GAME_WINDOW_NAME, gameWindow);

        setJMenuBar(new MenuBarFactory(this).createMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    public void exitApplication() {
        saveAllWindowsState();
        configManager.save();

        int result = JOptionPane.showConfirmDialog(this,
                "Вы действительно хотите выйти из приложения?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            RobotsProgram.setRussianUIManagerText();
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // ignore
        }
    }

    private void loadMainWindowState() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defaultWidth = screenSize.width - inset * 2;
        int defaultHeight = screenSize.height - inset * 2;

        setBounds(configManager.getMainWindowX(inset),
                configManager.getMainWindowY(inset),
                configManager.getMainWindowWidth(defaultWidth),
                configManager.getMainWindowHeight(defaultHeight));
        setExtendedState(configManager.getMainWindowState(JFrame.NORMAL));
    }

    private void loadInternalWindowState(String windowName, JInternalFrame frame) {
        if (!configManager.hasWindow(windowName)) return;

        int x = configManager.getWindowX(windowName, frame.getX());
        int y = configManager.getWindowY(windowName, frame.getY());
        int w = configManager.getWindowWidth(windowName, frame.getWidth());
        int h = configManager.getWindowHeight(windowName, frame.getHeight());
        boolean icon = configManager.getWindowIcon(windowName, false);
        boolean maximized = configManager.getWindowMaximized(windowName, false);

        frame.setBounds(x, y, w, h);
        try {
            if (maximized) {
                frame.setMaximum(true);
            } else if (icon) {
                frame.setIcon(true);
            }
        } catch (java.beans.PropertyVetoException e) {
            Logger.debug("Не удалось восстановить состояние окна " + windowName + ": " + e.getMessage());
        }
    }

    private void saveAllWindowsState() {
        configManager.setMainWindowBounds(getX(), getY(), getWidth(), getHeight(), getExtendedState());

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String windowName = getWindowNameByTitle(frame.getTitle());
            if (windowName != null) {
                configManager.setWindowBounds(windowName,
                        frame.getX(), frame.getY(),
                        frame.getWidth(), frame.getHeight(),
                        frame.isIcon(), frame.isMaximum());
            }
        }
    }

    private String getWindowNameByTitle(String title) {
        if (LOG_WINDOW_TITLE.equals(title)) return LOG_WINDOW_NAME;
        if (GAME_WINDOW_TITLE.equals(title)) return GAME_WINDOW_NAME;
        return null;
    }
}
