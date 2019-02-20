package cn.zmy.easymessenger;

/**
 * Created by zmy on 2019/2/19.
 */

public interface DoubleCallback
{
    void onSuccess(double result);

    void onError(Exception ex);
}
