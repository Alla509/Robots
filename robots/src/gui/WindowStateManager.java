package gui;

import config.ConfigManager;
import log.Logger;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class WindowStateManager {
    private final ConfigManager config;

    public WindowStateManager(ConfigManager config) {
        this.config = config;
    }

    public void loadMainWindowState(JFrame mainFrame) {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defaultW = screenSize.width - inset * 2;
        int defaultH = screenSize.height - inset * 2;

        mainFrame.setBounds(
                config.getMainWindowX(inset),
                config.getMainWindowY(inset),
                config.getMainWindowWidth(defaultW),
                config.getMainWindowHeight(defaultH)
        );
        mainFrame.setExtendedState(config.getMainWindowState(JFrame.NORMAL));
    }

    public void saveMainWindowState(JFrame mainFrame) {
        config.setMainWindowBounds(
                mainFrame.getX(), mainFrame.getY(),
                mainFrame.getWidth(), mainFrame.getHeight(),
                mainFrame.getExtendedState()
        );
    }

    public void loadInternalWindowState(JInternalFrame frame, String windowName) {
        int x = frame.getX();
        int y = frame.getY();
        int w = frame.getWidth();
        int h = frame.getHeight();
        boolean icon = false;
        boolean maximized = false;
        boolean visible = true;

        if (config.hasWindow(windowName)) {
            x = config.getWindowX(windowName, x);
            y = config.getWindowY(windowName, y);
            w = config.getWindowWidth(windowName, w);
            h = config.getWindowHeight(windowName, h);
            icon = config.getWindowIcon(windowName, false);
            maximized = config.getWindowMaximized(windowName, false);
            visible = config.getWindowVisible(windowName, true);
        }

        frame.setBounds(x, y, w, h);
        try {
            if (maximized) {
                frame.setMaximum(true);
            } else if (icon) {
                frame.setIcon(true);
            }
        } catch (java.beans.PropertyVetoException e) {
            Logger.debug("Property veto exception");
        }
        frame.setVisible(visible);
    }

    public void saveInternalWindowState(JInternalFrame frame, String windowName) {
        config.setWindowBounds(windowName,
                frame.getX(), frame.getY(),
                frame.getWidth(), frame.getHeight(),
                frame.isIcon(), frame.isMaximum()
        );
        config.setWindowVisible(windowName, frame.isVisible());
    }

    public void setupWindowBehavior(JInternalFrame frame, String windowName) {
        frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                config.setWindowVisible(windowName, false);
                config.save();
            }
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
                config.setWindowVisible(windowName, true);
                config.save();
            }
        });
    }
}
