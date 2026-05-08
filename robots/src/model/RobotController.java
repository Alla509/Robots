package model;

import javax.swing.Timer;
public class RobotController {
    private final RobotModel model;
    private final Timer timer;

    public RobotController(RobotModel model) {
        this.model = model;
        this.timer = new Timer(10, e -> updateModel());
        timer.start();
    }

    public void setTarget(int x, int y) {
        model.setTarget(x, y);
    }

    public void stopTimer() {
        if (timer.isRunning()) timer.stop();
    }

    public void updateModel() {
        model.update( 10);
    }
}
