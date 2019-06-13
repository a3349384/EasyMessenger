package cn.zmy.easymessenger;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

/**
 * Client Helper基类
 * */
public abstract class BaseClientHelper<T>
{
    private Context mAppContext;
    private volatile IBinder mIBinder;
    protected T mClient;

    public void __init(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public void __startBindServer() {
        if (__isBinderAlive()) {
            return;
        }
        synchronized (this) {
            if (__isBinderAlive()) {
                return;
            }
            Cursor cursor = null;
            Uri uri = Uri.parse(String.format("content://%s/", __getBinderKey()));
            cursor = mAppContext.getContentResolver().query(uri, null,null,
                    null, null);
            if (cursor == null) {
                throw new BinderException("Query server ContentProvider failed.");
            } else {
                mIBinder = cursor.getExtras().getBinder("binder");
                cursor.close();
                if (mIBinder == null) {
                    throw new BinderException("Get server binder failed.");
                }
                mClient = __getClientWithBinder(mIBinder);
            }
        }
    }

    public boolean __isBinderAlive() {
        return mIBinder != null && mIBinder.isBinderAlive();
    }

    protected abstract T __getClientWithBinder(IBinder binder);

    protected abstract String __getBinderKey();
}
