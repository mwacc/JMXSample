package jmxsample;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class SimpleAgent {

    private MBeanServer mbs = null;

    public SimpleAgent() throws Exception {

        // Get the platform MBeanServer
        mbs = ManagementFactory.getPlatformMBeanServer();

        // Unique identification of MBeans
        ElectroCar car = new ElectroCar();
        ObjectName carBean = null;

        // Uniquely identify the MBeans and register them with the platform MBeanServer
        carBean = new ObjectName("FOO:name=jmxsample.ElectroCar");
        mbs.registerMBean(car, carBean);

    }

    /**
     * Run with:
     * -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1617
     * -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
     */
    public static void main(String argv[]) throws Exception {
        SimpleAgent agent = new SimpleAgent();
        System.out.println("jmxsample.SimpleAgent is running...");
        Thread.sleep(5*60*1000); // sleep for 5 minutes
    }

}
