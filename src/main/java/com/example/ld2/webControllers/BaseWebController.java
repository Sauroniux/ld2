package com.example.ld2.webControllers;

import com.example.ld1.data.dbBase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;

public abstract class BaseWebController<T extends dbBase>
{
    //docker start my-own-mysql
    //docker start my-own-phpmyadmin

    private Class<T> type;

    protected BaseWebController(Class<T> type)
    {
        this.type = type;
    }

    protected Gson getGson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    protected abstract List<T> getAllFromDb();
    protected abstract T getByIdFromDb(int id);
    protected abstract void updateInDb(T object);
    protected abstract void deleteFromDb(int id);

    protected String getAll()
    {
        Gson gson = getGson();
        var all = getAllFromDb();
        return gson.toJson(all);
    }

    protected String update(String request, int id) throws InvocationTargetException, IllegalAccessException {
        try
        {
            Gson gson = getGson();
            T objectFromRequest = gson.fromJson(request, type);

            T objectFromDb = getByIdFromDb(id);

            var idTemp = objectFromDb.getId();
            if(objectFromDb == null || objectFromDb.getId() == 0)
                return "Couldn't find the requested object";

            objectFromDb = setAllExceptId(objectFromRequest, objectFromDb);

            updateInDb(objectFromDb);

            return "Success";
        }
        catch (Exception e)
        {
            return "Fail" + e.toString() + "\n Maybe the object with id " + id + " doesn't exist?";
        }
    }

    protected String create(String request) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Gson gson = getGson();
        T objectFromRequest = gson.fromJson(request, type);

        var constructors = type.getDeclaredConstructors();
        Constructor emptyConstructor = null;

        for(var constructor : constructors)
            if(constructor.getParameterCount() == 0)
                emptyConstructor = constructor;

        if(emptyConstructor == null)
            return "No default constructor found!";

        var newObject = setAllExceptId(objectFromRequest, (T)emptyConstructor.newInstance());

        updateInDb(newObject);

        return "Success";
    }

    protected String delete(int id)
    {
        deleteFromDb(id);

        var result = getByIdFromDb(id);

        if(result == null || result.getId() == 0)
            return "Success";

        return "Success";
    }

    private T setAllExceptId(T newObject, T objectFromDb) throws IllegalAccessException, InvocationTargetException {
        List<Method> allMethods = new LinkedList<>();
        allMethods.addAll(Arrays.asList(type.getDeclaredMethods()));
        var baseType = type.getSuperclass();

        if(baseType != null)
        {
            var baseMethods =baseType.getDeclaredMethods();
            allMethods.addAll(Arrays.asList(baseMethods));
        }

        List<AccessPair> pairs = new ArrayList<>();

        for(var method : allMethods)
        {
            if(!method.getName().startsWith("set"))
                continue;

            var setter = method;

            var getterName  = new StringBuilder(setter.getName());
            getterName.setCharAt(0, 'g');

            var getter = findMethodByName(allMethods, getterName.toString());

            if(getter == null)
                continue;

            pairs.add(new AccessPair(getter, setter));
        }

        for(var pair : pairs)
        {
            var value = pair.getter.invoke(newObject);
            pair.setter.invoke(objectFromDb, value);
        }

        return objectFromDb;
    }

    private static Method findMethodByName(Collection<Method> methodList, String name) {
        return methodList.stream().filter(carnet -> name.equals(carnet.getName())).findFirst().orElse(null);
    }

    public class AccessPair
    {
        private Method getter;
        private Method setter;

        public AccessPair(Method getter, Method setter){
            this.getter = getter;
            this.setter = setter;
        }

        public Method getGetter() {
            return getter;
        }

        public void setGetter(Method getter) {
            this.getter = getter;
        }

        public Method getSetter() {
            return setter;
        }

        public void setSetter(Method setter) {
            this.setter = setter;
        }
    }
}
