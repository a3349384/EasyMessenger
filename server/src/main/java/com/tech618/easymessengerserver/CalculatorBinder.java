package com.tech618.easymessengerserver;

import android.os.Binder;


/**
 * Created by zmy on 2018/4/6.
 */

public class CalculatorBinder extends Binder
{
    static final int TRANSACTION_add = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

    private CalculatorServerImpl mCalculator;

    public CalculatorBinder()
    {
        mCalculator = new CalculatorServerImpl();
    }

    @Override
    public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
        switch (code)
        {
            case TRANSACTION_add:
            {
                int _arg0;
                _arg0 = data.readInt();
                int _arg1;
                _arg1 = data.readInt();
                int _result = mCalculator.intTest(_arg0, _arg1);
                reply.writeNoException();
                reply.writeInt(_result);
                return true;

            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
