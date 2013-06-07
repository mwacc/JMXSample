package multithreading.impl;


import com.higherfrequencytrading.affinity.AffinityLock;
import com.higherfrequencytrading.affinity.AffinityStrategies;
import multithreading.RunSimpleQueue;

public class RunSimpleQueueAffinity extends RunSimpleQueue {

    protected void createAndStartThread(String name) {
        AffinityLock al = AffinityLock.acquireLock();
        try {
            new Thread(new ReaderQueue(q), name+"-r").start();
            new Thread(new WriterQueue(q), name+"-w").start();
        } finally {
            al.release();
        }
    }

}
