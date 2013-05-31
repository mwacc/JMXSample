package jmxsample;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Date;

public class JMXConsole {

    protected void run(boolean setValue, String objectName, String attributeName, String newValue,
                    String host, int port)
            throws IOException, MalformedObjectNameException, InstanceNotFoundException,
            MBeanException, AttributeNotFoundException, ReflectionException, InvalidAttributeValueException {

        //String host = "localhost";  // or some A.B.C.D
        //int port = 1617;
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
        JMXServiceURL serviceUrl = new JMXServiceURL(url);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);

        try {
            MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
            // now query to get the beans or whatever
            if( setValue ) {
                mbeanConn.setAttribute(new ObjectName(objectName), new Attribute(attributeName, getValue(newValue)));
            } else {
                for(String s : attributeName.split(",")) {
                    Object v = mbeanConn.getAttribute(new ObjectName(objectName), s);
                    System.out.println( String.format("Attribute %s has value %s",
                            s, v != null ? v.toString() : "NULL") );
                }
            }
        } finally {
            jmxConnector.close();
        }

    }

    /** ObjectName attributeName [new_value_if_set] host port
     * i.e.
     * read all attributes: java jmxsample.JMXConsole FOO:name=jmxsample.ElectroCar MaxSpeed,CurrentSpeed localhost 1617
     * set new MaxSpeed: java jmxsample.JMXConsole FOO:name=jmxsample.ElectroCar MaxSpeed i250 localhost 1617
     * */
    public static void main(String argv[])
            throws Exception {
        JMXConsole console = new JMXConsole();
        if(argv.length == 4) {
            console.run(false, argv[0], argv[1], null, argv[2], Integer.parseInt(argv[3]));
        } else if(argv.length == 5) {
            console.run(true, argv[0], argv[1], argv[2], argv[3], Integer.parseInt(argv[4]));
        }
    }

    private Object getValue(String v) {
        switch (v.charAt(0)){
            case 'i':
                return Integer.parseInt( v.substring(1) );
            case 'L':
                return Long.parseLong( v.substring(1) );
            case 'f':
                return Float.parseFloat( v.substring(1) );
            case 'D':
                return Double.parseDouble( v.substring(1) );
            case 't':
                return new Date( Long.parseLong(v.substring(1)) );
            case 'S':
                return v.substring(1);
            case 'b':
                return Boolean.valueOf(v.substring(1));
            default:
                throw new IllegalArgumentException(v);
        }
    }
}
