
import java.lang.*;
import java.lang.IllegalMonitorStateException;
import java.lang.InterruptedException;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.*;

public class ThreadDemo implements Runnable
{
    Lock lock=new Lock();
    public static void main(String[] args)
    {
        for(int i=0;i<10; i++){
            new Thread(new ThreadDemo()).start();
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
    class Lock
    {
        private boolean isLocked=false;
        private Thread lockedThread=null;

        public void lock() throws InterruptedException{
            while(isLocked){
                wait();
            }
            isLocked=true;
            lockedThread=Thread.currentThread();
        }
        public void unlock()
        {
            if(lockedThread!=Thread.currentThread()){
                throw new IllegalMonitorStateException("Calling thread is not equals to locked thread");
            }
            isLocked=false;
            lockedThread=null;
            notify();
        }
    }
}
