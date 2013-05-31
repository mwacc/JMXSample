package jmxsample;

import java.util.Random;

public class ElectroCar implements ElectroCarMBean {

    public int maxSpeed = 150;
    private Random rnd = new Random();

    @Override
    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public int getMaxSpeed() {
        return this.maxSpeed;
    }

    @Override
    public int getCurrentSpeed() {
        return rnd.nextInt(this.maxSpeed);
    }
}
