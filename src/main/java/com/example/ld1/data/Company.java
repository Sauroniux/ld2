package com.example.ld1.data;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
public class Company extends BaseUser implements Serializable
{
    private String companyName;
    private String companyEmail;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy = "courseCompanyModerator", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch =  FetchType.EAGER)
    @SortNatural
    private SortedSet<Course> moderatedCourses = new TreeSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "creator", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch =  FetchType.EAGER)
    @SortNatural
    private SortedSet<Course> ownedCourses = new TreeSet<>();

    public Company(){}

    @Override
    public void Cleanup()
    {
        moderatedCourses.clear();
        ownedCourses.clear();
    }

    @Override
    public boolean DoesModerateCourse(Course course)
    {
        if(moderatedCourses == null)
            return false;

        return moderatedCourses.contains(course);
    }

    public Company(String username, String password, String companyName, String companyEmail)
    {
        super(username, password);
        this.companyName = companyName;
        this.companyEmail = companyEmail;
    }

    public boolean IsCreatorOfTheCourse(Course course)
    {
        return ownedCourses.contains(course);
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getCompanyEmail()
    {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail)
    {
        this.companyEmail = companyEmail;
    }

    public SortedSet<Course> getModeratedCourses()
    {
        return moderatedCourses;
    }

    public void setModeratedCourses(SortedSet<Course> moderatedCourses)
    {
        this.moderatedCourses = moderatedCourses;
    }

    public SortedSet<Course> getOwnedCourses()
    {
        return ownedCourses;
    }

    public void setOwnedCourses(SortedSet<Course> ownedCourses)
    {
        this.ownedCourses = ownedCourses;
    }
}
