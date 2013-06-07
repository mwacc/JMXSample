package multithreading.impl;

import multithreading.RunSimpleQueue;

import java.util.Queue;

public class RunSimpleQueueTraditional extends RunSimpleQueue {

    protected void createAndStartThread(String name) {
        new Thread(new ReaderQueue(q), name+"-r").start();
        new Thread(new WriterQueue(q), name+"-w").start();
    }



}
