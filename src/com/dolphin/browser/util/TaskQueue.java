package com.dolphin.browser.util;

import com.danliu.util.Log;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {
    private static final String TAG = "TaskQueue";
    private BlockingQueue<Runnable> mTaskBlockQueue = new LinkedBlockingQueue<Runnable>();
    private boolean mStarted;
    private Thread mWorkingThread;
    private int mDelay;

    public TaskQueue(int delay) {
        mDelay = delay;
    }

    public void put(Runnable r) {
        boolean success = mTaskBlockQueue.offer(r);
        if (!success) {
            Log.e(TAG, "Enqueue task failed");
        }
    }

    public synchronized void start() {
        if (mWorkingThread == null) {
            mStarted = true;
            mWorkingThread = new WorkingThread();
            mWorkingThread.start();
        }
    }

    public synchronized void stop() {
        if (mWorkingThread != null) {
            try {
                mWorkingThread.interrupt();
            } catch (Exception e) {
                Log.w(e);
            }
            mWorkingThread = null;
        }

        mStarted = false;
    }

    private class WorkingThread extends Thread {

        public WorkingThread() {
            setName("TaskQueue Thread");
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run() {

            if (mDelay != 0) {
                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            while (mStarted && !interrupted()) {
                try {
                    Runnable r = mTaskBlockQueue.take();
                    r.run();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }
}
