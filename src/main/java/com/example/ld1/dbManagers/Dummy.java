package com.example.ld1.dbManagers;

import java.lang.reflect.ParameterizedType;

public class Dummy<T>
{
    public Class<T> getType()
    {
        var getClass = getClass();
        var superClass = getClass.getGenericSuperclass();
        
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
