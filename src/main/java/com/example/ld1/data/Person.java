package com.example.ld1.data;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SortNatural;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
public class Person extends com.example.ld1.data.BaseUser implements Serializable
{
    private String firstName;
    private String secondName;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch =  FetchType.EAGER)
    @SortNatural
    private SortedSet<Course> accessibleCourses = new TreeSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy = "coursePersonModerator", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch =  FetchType.EAGER)
    @SortNatural
    private SortedSet<Course> moderatedCourses = new TreeSet<>();

    public Person()
    {
    }

    @Override
    public void Cleanup()
    {
        moderatedCourses.clear();
        accessibleCourses.clear();
    }

    @Override
    public boolean DoesModerateCourse(Course course)
    {
        if(moderatedCourses == null)
            return false;

        return moderatedCourses.contains(course);
    }

    public Person(String username, String password, String name, String surname)
    {
        super(username, password);
        this.firstName = name;
        this.secondName = surname;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String name)
    {
        this.firstName = name;
    }

    public String getSecondName()
    {
        return secondName;
    }

    public void setSecondName(String surname)
    {
        this.secondName = surname;
    }

    public SortedSet<Course> getAccessibleCourses()
    {
        return accessibleCourses;
    }

    public void setAccessibleCourses(SortedSet<Course> accessibleCourses)
    {
        this.accessibleCourses = accessibleCourses;
    }

    public SortedSet<Course> getModeratedCourses()
    {
        return moderatedCourses;
    }

    public void setModeratedCourses(SortedSet<Course> moderatedCourses)
    {
        this.moderatedCourses = moderatedCourses;
    }
}
