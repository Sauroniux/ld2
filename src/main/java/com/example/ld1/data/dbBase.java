package com.example.ld1.data;

public abstract class dbBase
{
    protected int id;

    public static <T> T as(Class<T> t, Object o) {
        return t.isInstance(o) ? t.cast(o) : null;
    }
    public <T> T as(Class<T> t) {
        return t.isInstance(this) ? t.cast(this) : null;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}