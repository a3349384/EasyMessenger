package com.tech618.easymessenger;

/**
 * Created by 82538 on 2018/4/30.
 */

public interface LongCallback {
    void onSuccess(long result);

    void onError(Exception ex);
}
