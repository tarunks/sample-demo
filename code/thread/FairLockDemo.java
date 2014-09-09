import java.lang.InterruptedException;
import java.lang.Runnable;
import java.util.List;
import java.util.ArrayList;

public class FairLockDemo implements Runnable{
    FairLock lock=new FairLock();
    public static void main(String[] args)
    {
        for(int i=0;i<10; i++){
            new Thread(new FairLockDemo()).start();
        }
    }
    public void run(){
        try {
            longRunningTask();
        }
        catch (InterruptedException exp){
            System.out.println(" Exception in running thread : " + Thread.currentThread().getName());
        }catch (java.lang.IllegalMonitorStateException exp){
            System.out.println("IllegalMonitorStateException :" + Thread.currentThread().getName());
        }
    }
    private  void longRunningTask() throws InterruptedException, java.lang.IllegalMonitorStateException{
        lock.lock();
        String name=Thread.currentThread().getName();
        System.out.println("Work is going to start by: "+name);
        Thread.sleep(50000);
        //name=Thread.currentThread().getName();
        System.out.println("Work has been finshed by: "+name);
        lock.unlock();
    }
    class FairLock
    {
        private boolean isLocked=false;
        private Thread lockedThread=null;
        private List<QueueObject> waitingThreads=new ArrayList<QueueObject>();

        public void lock() throws InterruptedException{
            QueueObject queueObject=new QueueObject();
            boolean isLockedForThisThread=true;

            synchronized (this){
                waitingThreads.add(queueObject);
            }
            while(isLockedForThisThread){
                synchronized (this){
                    isLockedForThisThread=isLocked || waitingThreads.get(0) != queueObject;
                }
            }
            if(!isLockedForThisThread){
                isLocked=true;
                waitingThreads.remove(queueObject);
                lockedThread=Thread.currentThread();
                return;
            }
            try{
                queueObject.doWait();
            }catch(InterruptedException e){
                synchronized (this){
                    waitingThreads.remove(queueObject);
                }
                throw e;
            }


        }
        public void unlock()
        {
            if(lockedThread!=Thread.currentThread()){
                throw new IllegalMonitorStateException("Calling thread is not equals to locked thread");
            }
            isLocked=false;
            lockedThread=null;
            if(waitingThreads.size()>0){
              waitingThreads.get(0).doNotify();
            }

        }
    }
    class QueueObject
    {
        private boolean isNotified=false;
        public synchronized void doWait() throws InterruptedException{
            while(!isNotified){
                this.wait();
            }
            this.isNotified=false;
        }
        public synchronized void doNotify()
        {
            this.isNotified=true;
            this.notify();
        }
        public boolean equals(Object o){
            return this==o;
        }
    }
}
