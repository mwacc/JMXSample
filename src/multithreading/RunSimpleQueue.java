package multithreading;


import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class RunSimpleQueue implements RunSimpleQueueMBean {

    private final Queue<String> q = new LinkedBlockingQueue<String>();
    private volatile long counter = 0;
    private volatile boolean stopThread = false;

    public static void main(String[] argv) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        // Get the platform MBeanServer
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        RunSimpleQueue t = new RunSimpleQueueTraditional();
        mbs.registerMBean((RunSimpleQueueMBean)t, new ObjectName("FOO:name=multithreading.RunSimpleQueue"));

        t.startExample();
    }

    private void startExample() {
        for(int i = 0; i < 4; i++) {
            createAndStartThread("Thread-"+i, new ReaderQueue(q), new WriterQueue(q));
        }
    }

    protected abstract void createAndStartThread(String name, Runnable reader, Runnable writer);

    class ReaderQueue implements Runnable {
        private final Queue<String> q;

        ReaderQueue(Queue<String> q) {
            this.q = q;
        }

        @Override
        public void run() {
            while (!stopThread) {
                System.out.println(q.poll());
                ++counter;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class WriterQueue implements Runnable {
        private final Queue<String> q;

        WriterQueue(Queue<String> q) {
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

    public long getCounter() {
        return counter;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

}
