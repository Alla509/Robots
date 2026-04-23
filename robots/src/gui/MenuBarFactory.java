package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MenuBarFactory {
    private final MainApplicationFrame frame;

    public MenuBarFactory(MainApplicationFrame frame) {
        this.frame = frame;
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createWindowsMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitItem.addActionListener(e -> frame.exitApplication());
        fileMenu.add(exitItem);
        return fileMenu;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V);

        JMenuItem system = new JMenuItem("Системная схема");
        system.addActionListener(e -> {
            frame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            frame.invalidate();
        });

        JMenuItem cross = new JMenuItem("Универсальная схема");
        cross.addActionListener(e -> {
            frame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            frame.invalidate();
        });

        menu.add(system);
        menu.add(cross);
        return menu;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);
        JMenuItem logMsg = new JMenuItem("Сообщение в лог");
        logMsg.addActionListener(e -> Logger.debug("Новая строка"));
        menu.add(logMsg);
        return menu;
    }

    private JMenu createWindowsMenu() {
        JMenu menu = new JMenu("Окна");
        menu.setMnemonic(KeyEvent.VK_W);

        JMenuItem showLog = new JMenuItem("Показать протокол");
        showLog.addActionListener(e -> showWindow("LogWindow"));

        JMenuItem showGame = new JMenuItem("Показать игровое поле");
        showGame.addActionListener(e -> showWindow("GameWindow"));

        JMenuItem showCoord = new JMenuItem("Показать координаты");
        showCoord.addActionListener(e -> showWindow("CoordWindow"));

        menu.add(showLog);
        menu.add(showGame);
        menu.add(showCoord);
        return menu;
    }

    private void showWindow(String windowName) {
        frame.showWindowByName(windowName);
    }
}
