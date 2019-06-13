package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.IntCallback;

public class IntTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private IntCallback mCallback;

    public IntTask(Callable callable, IntCallback callback, BaseClientHelper helper)
    {
        mCallable = callable;
        mCallback = callback;
        mClientHelper = helper;
    }

    @Override
    public void run()
    {
        ThreadPoolManager.instance.submit(new Runnable()
        {
            @Override
            public void run()
            {
                int result;
                try
                {
                    mClientHelper.__startBindServer();
                    result = (int) mCallable.call();
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
