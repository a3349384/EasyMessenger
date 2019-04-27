package cn.zmy.easymessenger;

/**
 * Created by zmy on 2019/2/19.
 */

public interface CharCallback
{
    void onSuccess(char result);

    void onError(Exception ex);
}
