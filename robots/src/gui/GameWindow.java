package gui;

import model.RobotController;
import model.RobotModel;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame {
    public GameWindow(RobotModel model, RobotController controller) {
        super("Игровое поле", true, true, true, true);
        GameVisualizer visualizer = new GameVisualizer(model, controller);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
