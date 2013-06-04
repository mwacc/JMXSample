package multithreading;

public class RunSimpleQueueTraditional extends RunSimpleQueue {

    protected void createAndStartThread(String name, Runnable reader, Runnable writer) {
        new Thread(reader, name+"-r").start();
        new Thread(writer, name+"-w").start();
    }

}
