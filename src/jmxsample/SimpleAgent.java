package jmxsample;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class SimpleAgent {

    private MBeanServer mbs = null;

    public SimpleAgent() {

        // Get the platform MBeanServer
        mbs = ManagementFactory.getPlatformMBeanServer();

        // Unique identification of MBeans
        ElectroCar car = new ElectroCar();
        ObjectName carBean = null;

        try {
            // Uniquely identify the MBeans and register them with the platform MBeanServer
            carBean = new ObjectName("FOO:name=jmxsample.ElectroCar");
            mbs.registerMBean(car, carBean);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) throws InterruptedException {
        SimpleAgent agent = new SimpleAgent();
        System.out.println("jmxsample.SimpleAgent is running...");
        Thread.sleep(5*60*1000); // sleep for 5 minutes
    }

}
