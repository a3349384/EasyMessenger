package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.ByteCallback;

public class ByteTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private ByteCallback mCallback;

    public ByteTask(Callable callable, ByteCallback callback, BaseClientHelper helper)
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
            byte result;
            try
            {
                result = (byte) mCallable.call();
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
