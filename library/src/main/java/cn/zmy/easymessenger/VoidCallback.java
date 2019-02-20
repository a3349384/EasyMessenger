package cn.zmy.easymessenger;

/**
 * Created by 82538 on 2018/4/30.
 */

public interface VoidCallback {
    void onSuccess();

    void onError(Exception ex);
}
