package cn.zmy.easymessenger;

/**
 * Created by 82538 on 2018/4/30.
 */

public interface ResultCallBack<T> {
    void onSuccess(T result);

    void onError(Exception ex);
}
