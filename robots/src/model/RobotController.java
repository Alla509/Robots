package model;

import javax.swing.Timer;
public class RobotController {
    private final RobotModel model;
    private final Timer timer;
    private final int TIME_CONST = 10;

    public RobotController(RobotModel model) {
        this.model = model;
        this.timer = new Timer(TIME_CONST, e -> updateModel());
        timer.start();
    }

    public void setTarget(int x, int y) {
        model.setTarget(x, y);
    }

    public void stopTimer() {
        if (timer.isRunning()) timer.stop();
    }

    public void updateModel() {
        model.update( TIME_CONST);
    }
}
