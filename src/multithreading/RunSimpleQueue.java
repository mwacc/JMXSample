package multithreading;


import multithreading.impl.RunSimpleQueueAffinity;
import multithreading.impl.RunSimpleQueueTraditional;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RunSimpleQueue implements RunSimpleQueueMBean {

    protected final Queue<String> q = new LinkedBlockingQueue<String>();
    protected volatile long counter = 0;
    protected volatile boolean stopThread = false;

    public static void main(String[] argv) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        // Get the platform MBeanServer
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        //RunSimpleQueue t = new RunSimpleQueueTraditional();
        RunSimpleQueue t = new RunSimpleQueueAffinity();

        mbs.registerMBean((RunSimpleQueueMBean)t, new ObjectName("FOO:name=multithreading.RunSimpleQueue"));

        t.startExample();
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
