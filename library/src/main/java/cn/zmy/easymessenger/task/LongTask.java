package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.IntCallback;
import cn.zmy.easymessenger.LongCallback;

public class LongTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private LongCallback mCallback;

    public LongTask(Callable callable, LongCallback callback, BaseClientHelper helper)
    {
        mCallable = callable;
        mCallback = callback;
        mClientHelper = helper;
    }

    @Override
    public void run()
    {
        if (!mClientHelper.__isServiceBind())
        {
            mClientHelper.__runAfterConnected(this);
            mClientHelper.__startBindService();
            return;
        }
        ThreadPoolManager.instance.submit(new Runnable()
        {
            @Override
            public void run()
            {
                long result;
                try
                {
                    result = (long) mCallable.call();
                }
                catch (Exception ex)
                {
                    if (mCallback != null)
                    {
                        mCallback.onError(ex);
                    }
                    return;
                }
                if (mCallback != null)
                {
                    mCallback.onSuccess(result);
                }
            }
        });
    }
}
