package cn.zmy.easymessenger.task;

import java.util.concurrent.Callable;

import cn.zmy.easymessenger.BaseClientHelper;
import cn.zmy.easymessenger.CharCallback;

public class CharTask implements Runnable
{
    private Callable mCallable;
    private BaseClientHelper mClientHelper;
    private CharCallback mCallback;

    public CharTask(Callable callable, CharCallback callback, BaseClientHelper helper)
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
                char result;
                try
                {
                    mClientHelper.__startBindServer();
                    result = (char) mCallable.call();
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
