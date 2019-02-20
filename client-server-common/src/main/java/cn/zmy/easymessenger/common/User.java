package cn.zmy.easymessenger.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zmy on 2018/4/8.
 */
public class User implements Parcelable
{
    private String name;
    private int age;

    public User(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
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
        dest.writeInt(this.age);
    }

    @Override
    public String toString()
    {
        return "name=" + name + ";age=" + age;
    }

    public User()
    {
    }

    protected User(Parcel in)
    {
        this.name = in.readString();
        this.age = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel source)
        {
            return new User(source);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };
}
