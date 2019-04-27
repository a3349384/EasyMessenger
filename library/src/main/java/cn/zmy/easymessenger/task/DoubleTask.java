package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.BooleanCallback;
import cn.zmy.easymessenger.DoubleCallback;

public class DoubleTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private DoubleCallback mCallback;

    public DoubleTask(Callable callable, DoubleCallback callback, BaseClientHelper helper)
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
                double result;
                try
                {
                    result = (double) mCallable.call();
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
