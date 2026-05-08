package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Point;

public class RobotModel {
    public static final String PROP_POSITION = "position";
    public static final String PROP_DIRECTION = "direction";
    public static final String PROP_TARGET = "target";

    private volatile double robotX = 100;
    private volatile double robotY = 100;
    private volatile double robotDirection = 0;
    private volatile int targetX = 150;
    private volatile int targetY = 100;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public double getRobotX() { return robotX; }
    public double getRobotY() { return robotY; }
    public double getRobotDirection() { return robotDirection; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }

    public void setTarget(int x, int y) {
        targetX = x;
        targetY = y;
        pcs.firePropertyChange(PROP_TARGET, null, new Point(x, y));
    }

    private void setRobotPosition(double x, double y) {
        robotX = x;
        robotY = y;
        pcs.firePropertyChange(PROP_POSITION, null, new Point((int)x, (int)y));
    }

    private void setRobotDirection(double direction) {
        double old = robotDirection;
        robotDirection = normalizeRadians(direction);
        pcs.firePropertyChange(PROP_DIRECTION, old, robotDirection);
    }

    // Логика движения
    private void update(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, RobotConstants.MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -RobotConstants.MAX_ANGULAR_VELOCITY,
                RobotConstants.MAX_ANGULAR_VELOCITY);

        double newX, newY;
        if (Math.abs(angularVelocity) < 1e-8) {
            newX = robotX + velocity * duration * Math.cos(robotDirection);
            newY = robotY + velocity * duration * Math.sin(robotDirection);
        } else {
            newX = robotX + (velocity / angularVelocity) *
                    (Math.sin(robotDirection + angularVelocity * duration) - Math.sin(robotDirection));
            newY = robotY - (velocity / angularVelocity) *
                    (Math.cos(robotDirection + angularVelocity * duration) - Math.cos(robotDirection));
        }
        double newDirection = robotDirection + angularVelocity * duration;

        setRobotPosition(newX, newY);
        setRobotDirection(newDirection);
    }

    public void update(double duration) {
        double dx = getTargetX() - getRobotX();
        double dy = getTargetY() - getRobotY();
        double distance = Math.hypot(dx, dy);
        if (distance < 0.5) return;

        double angleToTarget = normalizeRadians(Math.atan2(dy, dx));
        double robotDir = getRobotDirection();
        double diff = angleToTarget - robotDir;
        diff = normalizeRadians(diff);
        if (diff > Math.PI) diff -= 2 * Math.PI;
        if (diff < -Math.PI) diff += 2 * Math.PI;

        double angularVelocity;
        if (Math.abs(diff) < 0.01) angularVelocity = 0;
        else angularVelocity = (diff > 0) ? RobotConstants.MAX_ANGULAR_VELOCITY : -RobotConstants.MAX_ANGULAR_VELOCITY;

        double velocity = (Math.abs(diff) < 0.3) ? RobotConstants.MAX_VELOCITY : 0;

        update(velocity, angularVelocity, duration);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private static double normalizeRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}
