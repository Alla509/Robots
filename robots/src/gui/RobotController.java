package gui;

public class RobotController {
    private final RobotModel model;
    private final double maxVelocity = 0.1;
    private final double maxAngularVelocity = 0.001;

    public RobotController(RobotModel model) {
        this.model = model;
    }

    public void updateModel() {
        double dx = model.getTargetX() - model.getRobotX();
        double dy = model.getTargetY() - model.getRobotY();
        double distance = Math.hypot(dx, dy);
        if (distance < 0.5) {
            return; // цель достигнута
        }

        double angleToTarget = normalizeRadians(Math.atan2(dy, dx));
        double robotDir = model.getRobotDirection();

        // Вычисляем кратчайшую разницу углов
        double diff = angleToTarget - robotDir;
        diff = normalizeRadians(diff);
        if (diff > Math.PI) diff -= 2 * Math.PI;
        if (diff < -Math.PI) diff += 2 * Math.PI;

        double angularVelocity;
        if (Math.abs(diff) < 0.01) {
            angularVelocity = 0;
        } else {
            angularVelocity = (diff > 0) ? maxAngularVelocity : -maxAngularVelocity;
        }

        // Если робот смотрит почти на цель – едем, иначе только поворачиваем
        double velocity = (Math.abs(diff) < 0.3) ? maxVelocity : 0;

        model.update(velocity, angularVelocity, 10); // duration = 10 мс
    }

    private double normalizeRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}
