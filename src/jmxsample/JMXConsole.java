package jmxsample;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    /** ObjectName attributeName [new_value_if_set] [host port ...]
     * i.e.
     * read all attributes: java jmxsample.JMXConsole FOO:name=jmxsample.ElectroCar MaxSpeed,CurrentSpeed localhost 1617
     * set new MaxSpeed: java jmxsample.JMXConsole FOO:name=jmxsample.ElectroCar MaxSpeed i250 localhost 1617
     * */
    public static void main(String argv[])
            throws Exception {
        JMXConsole console = new JMXConsole();

        if(argv.length == 1)
            argv = console.readFromFile(argv[0]);

        for(int i = argv.length % 2 == 0 ? 2 : 3; i < argv.length; i += 2) {
            try {
                console.run(argv.length % 2 == 1, argv[0], argv[1],
                    argv.length % 2 == 1 ? argv[2] : null, argv[i], Integer.parseInt(argv[i+1]));
            } catch (Exception e) {
                System.err.println(String.format("Can't perform operation on %s:%s", argv[i], argv[i + 1]));
            }
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

    private String[] readFromFile(String pathToFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathToFile));
        List<String> list = new ArrayList<String>();
        String line = null;
        while ((line = br.readLine()) != null) {
            list.addAll(Arrays.asList(line.split("\\s") ) );
        }
        br.close();

        return list.toArray(new String[list.size()]);
    }
}
