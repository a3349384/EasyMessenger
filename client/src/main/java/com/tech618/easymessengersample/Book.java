package com.tech618.easymessengersample;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zmy on 2019/2/20.
 */

public class Book implements Parcelable
{
    private String name;
    private String price;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.name);
        dest.writeString(this.price);
    }

    public Book()
    {
    }

    protected Book(Parcel in)
    {
        this.name = in.readString();
        this.price = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>()
    {
        @Override
        public Book createFromParcel(Parcel source)
        {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size)
        {
            return new Book[size];
        }
    };
}
