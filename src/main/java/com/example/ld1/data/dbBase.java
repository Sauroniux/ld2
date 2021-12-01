package com.example.ld1.data;

import javax.persistence.*;

@MappedSuperclass
public abstract class dbBase
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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
