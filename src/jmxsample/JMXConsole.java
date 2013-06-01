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
                    System.out.println( String.format("[%s:%d] Attribute %s has value %s",
                            host, port, s, v != null ? v.toString() : "NULL") );
                }
            }
        } finally {
            jmxConnector.close();
        }

    }

    /**
     * first argument - path to text file w/ commands
     * seconds argument - {1|0} continue or stop on errors, continue by default
     * Read batch command from file, all command will be executed one by one
     * ObjectName attributeName [new_value_if_set] [host port ...]
     * */
    public static void main(String argv[])
            throws Exception {
        JMXConsole console = new JMXConsole();

        boolean breakOnFail = false;
        if(argv.length == 2)
            breakOnFail = argv[1].equals("0");

        List<String> commands = console.readFromFile(argv[0]);

        for(String command : commands) {
            String[] arguments = command.split("\\s");
            try {
                console.run(arguments.length % 2 == 1, arguments[0], arguments[1],
                        arguments.length % 2 == 1 ? arguments[2] : null,
                        arguments[arguments.length-2], Integer.parseInt(arguments[arguments.length-1]));
            } catch (Exception e) {
                System.err.println(String.format("Can't perform command %s\n",
                        command));
                if( breakOnFail ) System.exit(-1);
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

    /**
     * One list is a completed JMX command:
     * ObjectName attributeName [new_value_if_set] [host port ...]
     * i.e.
     * read all attributes: java jmxsample.JMXConsole FOO:name=jmxsample.ElectroCar MaxSpeed,CurrentSpeed localhost 1617
     * set new MaxSpeed: java jmxsample.JMXConsole FOO:name=jmxsample.ElectroCar MaxSpeed i250 localhost 1617
     */
    private List<String> readFromFile(String pathToFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathToFile));
        List<String> list = new ArrayList<String>();
        String line = null;
        while ((line = br.readLine()) != null) {
            if(!line.startsWith("#") && line.length() > 0)
                list.add(line);
        }
        br.close();

        return list;
    }
}
