package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.BooleanCallback;

public class BooleanTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private BooleanCallback mCallback;

    public BooleanTask(Callable callable, BooleanCallback callback, BaseClientHelper helper)
    {
        mCallable = callable;
        mCallback = callback;
        mClientHelper = helper;
    }

    @Override
    public void run()
    {
        if (mClientHelper.__isServiceBind())
        {
            boolean result;
            try
            {
                result = (boolean) mCallable.call();
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
        else
        {
            mClientHelper.__runAfterConnected(this);
            mClientHelper.__startBindService();
        }
    }
}
