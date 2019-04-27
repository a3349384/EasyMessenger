package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.FloatCallback;

public class FloatTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private FloatCallback mCallback;

    public FloatTask(Callable callable, FloatCallback callback, BaseClientHelper helper)
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
            float result;
            try
            {
                result = (float) mCallable.call();
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
