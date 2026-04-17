package gui;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RobotCoordinatesWindow extends JInternalFrame implements PropertyChangeListener {
    private final JLabel positionLabel;
    private final JLabel directionLabel;
    private final RobotModel model;

    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        model.addPropertyChangeListener(this);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        positionLabel = new JLabel();
        directionLabel = new JLabel();
        panel.add(positionLabel);
        panel.add(directionLabel);
        getContentPane().add(panel);
        pack();
        setSize(200, 80);
        updateLabels();
    }

    private void updateLabels() {
        positionLabel.setText(String.format("Позиция: (%.1f, %.1f)", model.getRobotX(), model.getRobotY()));
        directionLabel.setText(String.format("Направление: %.1f°", Math.toDegrees(model.getRobotDirection())));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (RobotModel.PROP_POSITION.equals(evt.getPropertyName()) ||
                RobotModel.PROP_DIRECTION.equals(evt.getPropertyName())) {
            updateLabels();
        }
    }
}
