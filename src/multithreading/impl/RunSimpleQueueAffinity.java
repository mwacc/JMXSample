package multithreading.impl;


import com.higherfrequencytrading.affinity.AffinityLock;
import com.higherfrequencytrading.affinity.AffinityStrategies;
import multithreading.RunSimpleQueue;

public class RunSimpleQueueAffinity extends RunSimpleQueue {

    protected void createAndStartThread(String name) {
        AffinityLock al = AffinityLock.acquireLock();
        try {
            // find a cpu on a different socket, otherwise a different core.
            AffinityLock readerLock = al.acquireLock(AffinityStrategies.SAME_SOCKET, AffinityStrategies.SAME_CORE);
            new Thread(new ReaderQueue(q), name+"-r").start();
            // find a cpu on the same core, or the same socket, or any free cpu.
            AffinityLock writerLock = readerLock.acquireLock(AffinityStrategies.SAME_SOCKET, AffinityStrategies.SAME_CORE);
            new Thread(new WriterQueue(q), name+"-w").start();
        } finally {
            al.release();
        }
    }

}
