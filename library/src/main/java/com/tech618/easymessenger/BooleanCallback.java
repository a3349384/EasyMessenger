package com.tech618.easymessenger;

/**
 * Created by 82538 on 2018/4/30.
 */

public interface BooleanCallback {
    void onSuccess(boolean result);

    void onError(Exception ex);
}
