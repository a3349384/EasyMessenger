package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.VoidCallback;

public class VoidTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private VoidCallback mCallback;

    public VoidTask(Callable callable, VoidCallback callback, BaseClientHelper helper)
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
                try
                {
                    mCallable.call();
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
                    mCallback.onSuccess();
                }
            }
        });
    }
}
