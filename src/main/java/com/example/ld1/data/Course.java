package com.example.ld1.data;

import com.example.ld1.dbManagers.DbManager;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
public class Course extends dbBase implements Comparable<Course>, Serializable
{
    private String name;
    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("id ASC")
    @LazyCollection(LazyCollectionOption.FALSE)
    @SortNatural
    private SortedSet<Company> courseCompanyModerator;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("id ASC")
    @LazyCollection(LazyCollectionOption.FALSE)
    @SortNatural
    private SortedSet<Person> coursePersonModerator;

    @ManyToMany(mappedBy = "accessibleCourses", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("id ASC")
    @LazyCollection(LazyCollectionOption.FALSE)
    @SortNatural
    private SortedSet<Person> viewers;

    @ManyToOne
    private Company creator;

    @OneToOne(cascade = CascadeType.ALL)
    private Folder rootFolder;

    public Course(){}

    public Course(String name, String description, Company creator)
    {
        this.name = name;
        this.description = description;
        this.creator = creator;
    }

    public void initRootFolder()
    {
        if(rootFolder != null)
            return;

        rootFolder = new Folder(null, "root_" + name, this);

        DbManager.getInstance().CreateT(rootFolder);
        DbManager.getInstance().EditT(this);
    }

    public void addModerator(Company company)
    {
        courseCompanyModerator.add(company);

        DbManager.getInstance().EditT(this);
    }

    public void AppendModerator(BaseUser user)
    {
        if(courseCompanyModerator == null)
            courseCompanyModerator = new TreeSet<>();

        if(user.isPerson())
            coursePersonModerator.add(user.as(Person.class));
        else if(user.isCompany())
            courseCompanyModerator.add(user.as(Company.class));

        DbManager.getInstance().InsertModerator(user, this);
    }

    public void AppendViewer(Person person)
    {
        if(viewers == null)
            viewers = new TreeSet<>();

        person.getAccessibleCourses().add(this);
        DbManager.getInstance().InsertViewer(person, this);
    }

    public boolean DoesPersonHaveAccess(Person person)
    {
        return viewers.contains(person);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Folder getRootFolder()
    {
        return rootFolder;
    }

    public void setRootFolder(Folder rootFolder)
    {
        this.rootFolder = rootFolder;
    }

    public SortedSet<Person> getViewers()
    {
        return viewers;
    }

    public void setViewers(SortedSet<Person> viewers)
    {
        this.viewers = viewers;
    }

    @Override
    public int compareTo(Course o)
    {
        if(o == null || o.name == null)
            return 1;

        return name.compareTo(o.name);
    }

    public Company getCreator()
    {
        return creator;
    }

    public void setCreator(Company creator)
    {
        this.creator = creator;
    }

    public SortedSet<Person> getCoursePersonModerator()
    {
        return coursePersonModerator;
    }

    public void setCoursePersonModerator(SortedSet<Person> coursePersonModerator)
    {
        this.coursePersonModerator = coursePersonModerator;
    }

    public SortedSet<Company> getCourseCompanyModerator()
    {
        return courseCompanyModerator;
    }

    public void setCourseCompanyModerator(SortedSet<Company> courseCompanyModerator)
    {
        this.courseCompanyModerator = courseCompanyModerator;
    }
}
