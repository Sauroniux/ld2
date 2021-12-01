package com.example.ld1.data;

import com.example.ld1.dbManagers.DbManager;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@MappedSuperclass
public abstract class BaseUser extends dbBase implements Comparable<BaseUser>
{
    private String username;
    private String password;
    private LocalDate dateUserCreated;
    private LocalDate dateLastModified;

    public BaseUser(String username, String password)
    {
        this.username = username;
        this.password = password;

        this.dateUserCreated = LocalDate.now();
        this.dateLastModified = LocalDate.now();
    }

    public BaseUser()
    {
    }

    public abstract void Cleanup();

    public abstract boolean DoesModerateCourse(Course course);

    public boolean isPerson()
    {
        Person result = as(Person.class, this);

        return result != null;
    }

    public boolean isCompany()
    {
        Company result = as(Company.class, this);

        return result != null;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public LocalDate getDateUserCreated()
    {
        return dateUserCreated;
    }

    public void setDateUserCreated(LocalDate dateUserCreated)
    {
        this.dateUserCreated = dateUserCreated;
    }

    public LocalDate getDateLastModified()
    {
        return dateLastModified;
    }

    public void setDateLastModified(LocalDate dateLastModified)
    {
        this.dateLastModified = dateLastModified;
    }

    public int compareTo(BaseUser st){
        if(st == null)
            return 1;

        if(dateUserCreated == null || st.dateUserCreated == null)
            if(st.username == null)
                return 1;
            else
                return username.compareTo(st.username);

        if(dateUserCreated.isBefore(st.dateUserCreated))
            return 1;
        else if(dateUserCreated.isEqual(st.dateUserCreated))
            return username.compareTo(st.username);
        else
            return -1;
    }
}
