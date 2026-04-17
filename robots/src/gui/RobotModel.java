package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
        int oldX = targetX;
        int oldY = targetY;
        targetX = x;
        targetY = y;
        pcs.firePropertyChange(PROP_TARGET, null, new java.awt.Point(x, y));
    }

    public void setRobotPosition(double x, double y) {
        double oldX = robotX;
        double oldY = robotY;
        robotX = x;
        robotY = y;
        pcs.firePropertyChange(PROP_POSITION, null, new java.awt.Point((int)x, (int)y));
    }

    public void setRobotDirection(double direction) {
        double oldDir = robotDirection;
        robotDirection = normalizeRadians(direction);
        pcs.firePropertyChange(PROP_DIRECTION, oldDir, robotDirection);
    }

    // Метод обновления модели по законам физики
    public void update(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

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
        double newDirection = normalizeRadians(robotDirection + angularVelocity * duration);

        setRobotPosition(newX, newY);
        setRobotDirection(newDirection);
    }

    private double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private double normalizeRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;
}
