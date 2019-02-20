package cn.zmy.easymessenger;

/**
 * Created by zmy on 2019/2/19.
 */

public interface ShortCallback
{
    void onSuccess(short result);

    void onError(Exception ex);
}
