package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.ResultCallback;

public class ResultTask<T> implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private ResultCallback<T> mCallback;

    public ResultTask(Callable callable, ResultCallback<T> callback, BaseClientHelper helper)
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
            T result;
            try
            {
                result = (T) mCallable.call();
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
