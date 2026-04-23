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
        robotDirection = RobotMath.normalizeRadians(direction);
        pcs.firePropertyChange(PROP_DIRECTION, old, robotDirection);
    }

    // Логика движения
    public void update(double velocity, double angularVelocity, double duration) {
        velocity = RobotMath.applyLimits(velocity, 0, RobotConstants.MAX_VELOCITY);
        angularVelocity = RobotMath.applyLimits(angularVelocity, -RobotConstants.MAX_ANGULAR_VELOCITY,
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
}
