package com.tech618.easymessengerclientservercommon;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zmy on 2018/4/9.
 */
public enum Color implements Parcelable
{
    RED, BLUE, GREEN;

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeString(name());
    }

    public static final Creator<Color> CREATOR = new Creator<Color>()
    {
        @Override
        public Color createFromParcel(final Parcel source)
        {
            return Color.valueOf(source.readString());
        }

        @Override
        public Color[] newArray(final int size)
        {
            return new Color[size];
        }
    };

}
