package cn.zmy.easymessenger.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager
{
    public static final ThreadPoolManager instance = new ThreadPoolManager();

    private ThreadPoolExecutor mExecutor;

    private ThreadPoolManager()
    {
        mExecutor = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors() * 2,
                10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(64, true),
                new ThreadFactory()
                {
                    private int mThreadIndex;

                    @Override
                    public Thread newThread(Runnable r)
                    {
                        return new Thread(r, "Thread-EasyMessenger" + mThreadIndex++);
                    }
                }, new ThreadPoolExecutor.AbortPolicy());
    }

    public void submit(Runnable runnable)
    {
        mExecutor.submit(runnable);
    }
}
