package model;

public class RobotController {
    private final RobotModel model;

    public RobotController(RobotModel model) {
        this.model = model;
    }

    public void updateModel() {
        double dx = model.getTargetX() - model.getRobotX();
        double dy = model.getTargetY() - model.getRobotY();
        double distance = Math.hypot(dx, dy);
        if (distance < 0.5) return;

        double angleToTarget = RobotMath.normalizeRadians(Math.atan2(dy, dx));
        double robotDir = model.getRobotDirection();
        double diff = angleToTarget - robotDir;
        diff = RobotMath.normalizeRadians(diff);
        if (diff > Math.PI) diff -= 2 * Math.PI;
        if (diff < -Math.PI) diff += 2 * Math.PI;

        double angularVelocity;
        if (Math.abs(diff) < 0.01) angularVelocity = 0;
        else angularVelocity = (diff > 0) ? RobotConstants.MAX_ANGULAR_VELOCITY : -RobotConstants.MAX_ANGULAR_VELOCITY;

        double velocity = (Math.abs(diff) < 0.3) ? RobotConstants.MAX_VELOCITY : 0;

        model.update(velocity, angularVelocity, 10);
    }
}
