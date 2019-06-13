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
        ThreadPoolManager.instance.submit(new Runnable()
        {
            @Override
            public void run()
            {
                T result;
                try
                {
                    mClientHelper.__startBindServer();
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
        });
    }
}
