package cn.zmy.easymessenger.contentprovider;

import android.database.AbstractCursor;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by zmy on 2019/6/13.
 */
public class BinderCursor extends AbstractCursor {
    public static final String KEY_BINDER = "binder";

    private Bundle binderExtras = new Bundle();

    public BinderCursor(IBinder binder) {
        binderExtras.putBinder(KEY_BINDER, binder);
    }

    @Override
    public Bundle getExtras() {
        return binderExtras;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{KEY_BINDER};
    }

    @Override
    public String getString(int column) {
        return null;
    }

    @Override
    public short getShort(int column) {
        return 0;
    }

    @Override
    public int getInt(int column) {
        return 0;
    }

    @Override
    public long getLong(int column) {
        return 0;
    }

    @Override
    public float getFloat(int column) {
        return 0;
    }

    @Override
    public double getDouble(int column) {
        return 0;
    }

    @Override
    public boolean isNull(int column) {
        return false;
    }
}