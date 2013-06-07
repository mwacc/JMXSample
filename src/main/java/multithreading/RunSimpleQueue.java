package multithreading;


import com.higherfrequencytrading.affinity.impl.NativeAffinity;
import multithreading.impl.RunSimpleQueueAffinity;
import multithreading.impl.RunSimpleQueueTraditional;

import javax.management.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RunSimpleQueue implements RunSimpleQueueMBean {

    protected final Queue<String> q = new LinkedBlockingQueue<String>();
    protected volatile long counter = 0;
    protected volatile boolean stopThread = false;

    public static void main(String[] argv) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, IOException {
        // Get the platform MBeanServer
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        //RunSimpleQueue t = new RunSimpleQueueTraditional();
        RunSimpleQueue t = new RunSimpleQueueAffinity();

        mbs.registerMBean((RunSimpleQueueMBean)t, new ObjectName("FOO:name=multithreading.RunSimpleQueue"));

        t.startExample();

        /*InputStream is = NativeAffinity.class.getResource("/").openStream();
        java.util.Scanner s = new java.util.Scanner(is);
        while (s.hasNext()) {
            System.out.println(s.next());
        } */
    }

    private void startExample() {
        for(int i = 0; i < 2; i++) {
            createAndStartThread("Thread-"+i);
        }
    }

    protected abstract void createAndStartThread(String name);

    public long getCounter() {
        return counter;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }


    protected class ReaderQueue implements Runnable {
        private final Queue<String> q;

        public ReaderQueue(Queue<String> q) {
            this.q = q;
        }

        @Override
        public void run() {
            while (!stopThread) {
                //System.out.println(q.poll());
                ++counter;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected class WriterQueue implements Runnable {
        private final Queue<String> q;

        public WriterQueue(Queue<String> q) {
            this.q = q;
        }

        @Override
        public void run() {
            while(!stopThread) {
                q.add("It's random String, believe me!");
                ++counter;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
