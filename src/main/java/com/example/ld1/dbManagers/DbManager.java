package com.example.ld1.dbManagers;

import com.example.ld1.data.*;
import com.example.ld1.fxControllers.SceneManager;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DbManager extends FactoryHelper
{
    private static DbManager instance;

    private BaseUser currentUser;

    private final List<Object> objectToRemove = new ArrayList<>();

    private DbManager()
    {
        super();
        currentUser = null;
    }

    public static DbManager getInstance()
    {
        if(instance == null)
            instance = new DbManager();

        return instance;
    }

    public Set<Course> getViewableCourses()
    {
        if(currentUser == null || currentUser.isCompany())
            return null;

        Person person = BaseUser.as(Person.class, currentUser);

        return person.getAccessibleCourses();
    }

    public Set<Course> getModeratedCourses()
    {
        if(currentUser == null)
            return null;

        if(currentUser.isCompany())
            return currentUser.as(Company.class).getModeratedCourses();
        else if(currentUser.isPerson())
            return currentUser.as(Person.class).getModeratedCourses();
        else
            return null;
    }

    public Company getCurrentCompany()
    {
        if(currentUser == null || currentUser.isPerson())
            return null;

        return BaseUser.as(Company.class, currentUser);
    }

    public Person getCurrentPerson()
    {
        if(currentUser == null || currentUser.isCompany())
            return null;

        return BaseUser.as(Person.class, currentUser);
    }

    public void UpdateCurrentUserFromDB()
    {
        if(currentUser.isCompany())
            currentUser = this.<Company>GetById(currentUser.getId(), Company.class);
        else if (currentUser.isPerson())
            currentUser = this.<Person>GetById(currentUser.getId(), Person.class);
    }

    // Checks ----------------------------------------------------

    public boolean doesUsernameExist(String username)
    {
        Set<Person> users = this.GetAll(Person.class);

        for(var user : users)
            if(user.getUsername().equals(username))
                return true;

        Set<Company> companies = this.GetAll(Company.class);

        for(var company : companies)
            if(company.getUsername().equals(username))
                return true;

        return false;
    }

    // Delete operations ------------------------------------------

    public void DeleteCurrentUser()
    {
        currentUser.Cleanup();

        if(currentUser.isPerson())
            this.<Person>DeletePerson(currentUser.as(Person.class));
        else if (currentUser.isCompany())
            this.<Company>DeleteCompany(currentUser.as(Company.class));
    }

    public void AddObjectToCleanup(Object o)
    {
        objectToRemove.add(o);
    }

    public void ExecuteCleanup()
    {
        EntityManager em = null;
        try
        {
            em = FactoryHelper.getEntityManager();
            em.getTransaction().begin();

            for(var o  : objectToRemove)
                em.remove(em.contains(o) ? o : em.merge(o));

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

    public void DeleteFileSystemItem(FileSystemItem item)
    {
        if(item.isFolder())
            this.RemoveById(item.getId(), Folder.class);
        else if (item.isFile())
            this.RemoveById(item.getId(), File.class);
    }

    public BaseUser GetByUsername(String username)
    {
        var allCompanies = this.<Company>GetAll(Company.class);
        var allPeople = this.<Person>GetAll(Person.class);

        if(allCompanies != null)
            for(var company : allCompanies)
            {
                if(company.getUsername().equals(username))
                    return company;
            }

        if(allPeople != null)
            for(var person : allPeople)
                if(person.getUsername().equals(username))
                    return person;

        return null;
    }

    public void DeleteCompany(Company company)
    {
        List<Query> queries = new ArrayList<>();
        EntityManager em = getEntityManager();

        Query query1 = em.createNativeQuery("DELETE FROM `Course_Company` WHERE `moderatedCourses_id` = :arg1");
        query1.setParameter("arg1", company.getId());

        Query query2 = em.createNativeQuery("DELETE FROM `Course` WHERE `creator_id` = :arg1");
        query2.setParameter("arg1", company.getId());

        Query query3 = em.createNativeQuery("DELETE FROM `Company` WHERE `id` = :arg1");
        query3.setParameter("arg1", company.getId());

        queries.add(query1);
        queries.add(query3);
        queries.add(query2);

        RunCustomCommands(queries, em);
    }

    public void InsertViewer(Person viewer, Course course)
    {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("INSERT INTO Person_Course (viewers_id, accessibleCourses_id) VALUES (:arg1, :arg2)");
        query.setParameter("arg1", viewer.getId());
        query.setParameter("arg2", course.getId());
        RunCustomCommand(query, em);
    }

    public void InsertModerator(BaseUser moderator, Course course)
    {
        EntityManager em = getEntityManager();
        Query query = null;
        if(moderator.isCompany())
        {
            query = em.createNativeQuery("INSERT INTO Course_Company (moderatedCourses_id, courseCompanyModerator_id) VALUES (:arg1, :arg2)");
        }
        else
        {
            query = em.createNativeQuery("INSERT INTO Course_Person (moderatedCourses_id, coursePersonModerator_id) VALUES (:arg1, :arg2)");
        }
        query.setParameter("arg1", course.getId());
        query.setParameter("arg2", moderator.getId());
        RunCustomCommand(query, em);
    }

    public void DeleteCourse(Course course)
    {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("DELETE FROM `Course` WHERE `id` = :arg1");
        query.setParameter("arg1", course.getId());
        RunCustomCommand(query, em);
    }

    public BaseUser CheckLogin(String username, String password)
    {
        Set<Person> allPeople = this.<Person>GetAll(Person.class);
        Set<Company> allCompanies = this.<Company>GetAll(Company.class);

        if(allPeople == null && allCompanies == null)
            return null;

        if(allPeople != null)
            for (BaseUser user: allPeople)
                if(Objects.equals(user.getUsername(), username) && Objects.equals(user.getPassword(), password))
                    return user;

        if(allCompanies != null)
            for (BaseUser user: allCompanies)
                if(Objects.equals(user.getUsername(), username) && Objects.equals(user.getPassword(), password))
                    return user;

        return null;
    }

    public void DeletePerson(Person person)
    {
        List<Query> queries = new ArrayList<>();
        EntityManager em = getEntityManager();

        Query query1 = em.createNativeQuery("DELETE FROM `Person_Course` WHERE `viewers_id` = :arg1");
        query1.setParameter("arg1", person.getId());


        Query query2 = em.createNativeQuery("DELETE FROM `Course_Person` WHERE `coursePersonModerator_id` = :arg1");
        query2.setParameter("arg1", person.getId());

        Query query3 = em.createNativeQuery("DELETE FROM `Person` WHERE `id` = :arg1");
        query3.setParameter("arg1", person.getId());

        queries.add(query1);
        queries.add(query2);
        queries.add(query3);

        RunCustomCommands(queries, em);
    }

    public BaseUser getCurrentUser()
    {
        return currentUser;
    }

    public void setCurrentUser(BaseUser currentUser)
    {
        this.currentUser = currentUser;
    }
}
