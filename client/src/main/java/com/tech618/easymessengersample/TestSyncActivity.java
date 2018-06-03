package com.tech618.easymessengersample;

import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by 82538 on 2018/6/3.
 */

public class TestSyncActivity extends AppCompatActivity{

    public static final String URI_SUFFIX="qiyi.svg.dispatcher";
    public static final String PROJECTION_MAIN[] = {"main"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sync);
        findViewById(R.id.buttonTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = null;
                try {

                    cursor = TestSyncActivity.this.getContentResolver().query(getDispatcherProviderUri(), PROJECTION_MAIN,
                            null, null, null);
                    if (cursor != null) {
                        IBinder binder = cursor.getExtras().getBinder("KeyBinderWrapper");
                        if (binder != null)
                        {
                            binder.transact(Binder.FIRST_CALL_TRANSACTION + 1, Parcel.obtain(), Parcel.obtain(), 0);
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } finally {
                    if(cursor != null)
                    {
                        cursor.close();
                    }
                }
            }
        });
    }

    private Uri getDispatcherProviderUri() {
        return Uri.parse("content://com.tech618.easymessengerserver.DispatcherProvider/main");
    }
}
