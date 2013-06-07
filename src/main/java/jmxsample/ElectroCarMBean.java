package jmxsample;

public interface ElectroCarMBean {
    public void setMaxSpeed(int maxSpeed);
    public int getMaxSpeed();
    public int getCurrentSpeed();
}
