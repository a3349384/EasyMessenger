package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.ShortCallback;

public class ShortTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private ShortCallback mCallback;

    public ShortTask(Callable callable, ShortCallback callback, BaseClientHelper helper)
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
            short result;
            try
            {
                result = (short) mCallable.call();
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
