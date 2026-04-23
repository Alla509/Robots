package gui;

import config.ConfigManager;
import model.RobotModel;
import model.RobotController;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainApplicationFrame extends JFrame {
    private static final String LOG_WINDOW_TITLE = "Протокол работы";
    private static final String GAME_WINDOW_TITLE = "Игровое поле";
    private static final String COORD_WINDOW_TITLE = "Координаты робота";
    private static final String LOG_WINDOW_NAME = "LogWindow";
    private static final String GAME_WINDOW_NAME = "GameWindow";
    private static final String COORD_WINDOW_NAME = "CoordWindow";

    private final JDesktopPane desktopPane = new JDesktopPane();
    private final ConfigManager configManager = new ConfigManager();
    private final WindowStateManager windowStateManager = new WindowStateManager(configManager);

    private final RobotModel robotModel;
    private final RobotController robotController;
    private final Timer timer;

    public MainApplicationFrame() {
        configManager.load();
        windowStateManager.loadMainWindowState(this);
        setContentPane(desktopPane);

        robotModel = new RobotModel();
        robotController = new RobotController(robotModel);

        LogWindow logWindow = createLogWindow();
        GameWindow gameWindow = new GameWindow(robotModel);
        RobotCoordinatesWindow coordWindow = new RobotCoordinatesWindow(robotModel);

        addWindow(LOG_WINDOW_NAME, logWindow);
        gameWindow.setSize(400, 400);
        addWindow(GAME_WINDOW_NAME, gameWindow);
        coordWindow.setSize(250, 100);
        coordWindow.setLocation(10, 400);
        addWindow(COORD_WINDOW_NAME, coordWindow);

        windowStateManager.loadInternalWindowState(logWindow, LOG_WINDOW_NAME);
        windowStateManager.loadInternalWindowState(gameWindow, GAME_WINDOW_NAME);
        windowStateManager.loadInternalWindowState(coordWindow, COORD_WINDOW_NAME);

        setJMenuBar(new MenuBarFactory(this).createMenuBar());

        timer = new Timer(10, e -> robotController.updateModel());
        timer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    private void addWindow(String windowName, JInternalFrame frame) {
        windowStateManager.setupWindowBehavior(frame, windowName);
        desktopPane.add(frame);
    }

    public void showWindowByName(String windowName) {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (windowName.equals(getWindowNameByTitle(frame.getTitle()))) {
                frame.setVisible(true);
                try {
                    if (frame.isIcon()) frame.setIcon(false);
                } catch (java.beans.PropertyVetoException e) {
                    Logger.debug("Property veto exception");
                }
                frame.toFront();
                break;
            }
        }
    }

    private LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    public void exitApplication() {
        if (timer != null && timer.isRunning()) timer.stop();

        windowStateManager.saveMainWindowState(this);
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String name = getWindowNameByTitle(frame.getTitle());
            if (name != null) {
                windowStateManager.saveInternalWindowState(frame, name);
            }
        }
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

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            RobotsProgram.setRussianUIManagerText();
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            Logger.debug("Exception");
        }
    }

    private String getWindowNameByTitle(String title) {
        if (LOG_WINDOW_TITLE.equals(title)) return LOG_WINDOW_NAME;
        if (GAME_WINDOW_TITLE.equals(title)) return GAME_WINDOW_NAME;
        if (COORD_WINDOW_TITLE.equals(title)) return COORD_WINDOW_NAME;
        return null;
    }
}
