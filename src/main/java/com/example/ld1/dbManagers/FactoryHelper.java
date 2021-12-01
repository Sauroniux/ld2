package com.example.ld1.dbManagers;

import com.example.ld1.data.dbBase;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FactoryHelper
{
    private static EntityManagerFactory emf;

    public FactoryHelper()
    {
        GetFactory();
    }

//    private <T> Class<T> GetType()
//    {
//        List<T> dummy = new ArrayList<>(0);
//        Type[] actualTypeArguments = ((ParameterizedType) dummy.getClass().getGenericSuperclass()).getActualTypeArguments();
//        Type clazz = actualTypeArguments[0];
//        Class<T> theClass = (Class<T>) clazz.getClass();
//
//        return theClass;
//
//        //var dummy = new Dummy<T>();
//        //return dummy.getType();T
//    }

    public static EntityManagerFactory GetFactory()
    {
        if (emf == null)
            emf = Persistence.createEntityManagerFactory("CoursePersistenceUnit");

        return emf;
    }

    public static EntityManager getEntityManager()
    {
        return GetFactory().createEntityManager();
    }

    public void RunCustomCommand(String string)
    {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("INSERT INTO Person_Course (viewers_id, accessibleCourses_id) VALUES (:arg1, :arg2)");
        RunCustomCommand(query, em);
    }

    public void RunCustomCommand(Query query, EntityManager em)
    {
        try
        {
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public void RunCustomCommands(List<Query> queries, EntityManager em)
    {
        try
        {
            em.getTransaction().begin();
            for(var query : queries)
                query.executeUpdate();
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public <T extends dbBase>void CreateT(T data)
    {
        EntityManager em = null;

        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(data);
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public <T extends dbBase> T EditT(T target)
    {
        EntityManager em = null;
        T result = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            result = em.merge(target);
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }

        return result;
    }

    public <T extends dbBase> void Remove(T data)
    {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            em.remove(data);
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public <T extends dbBase> void RemoveById(int id, Class<T> type)
    {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();

            T data = null;
            try
            {
                data = em.getReference(type, id);
                data.getId();
            }
            catch (Exception e)
            {
                System.out.println("No such T by given Id");
            }
            em.remove(data);
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }
    public <T extends dbBase> Set<T> GetAll(Class<T> type)
    {
        return this.<T>GetAll(true, -1, -1, type);
    }

    public <T extends dbBase> Set<T> GetAll(boolean all, int resMax, int resFirst, Class<T> type)
    {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery query = em.getCriteriaBuilder().createQuery();
            query.select(query.from(type));
            Query q = em.createQuery(query);

            if (!all)
            {
                q.setMaxResults(resMax);
                q.setFirstResult(resFirst);
            }

            return new HashSet<T>(q.getResultList());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (em != null)
            {
                em.close();
            }
        }
        return null;
    }
    public <T extends dbBase> T GetById(int id, Class<T> type)
    {
        EntityManager em = null;

        T user = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            user = em.getReference(type, id);
            user.getId();
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            System.out.println("No such user by given Id");
        }
        return user;
    }
}
