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
public class Folder extends FileSystemItem implements Serializable
{
    @OneToOne(mappedBy = "rootFolder", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Course parentCourse;

    @ManyToOne(cascade = CascadeType.ALL)
    private Folder parent;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("id ASC")
    @LazyCollection(LazyCollectionOption.FALSE)
    @SortNatural
    private SortedSet<Folder> subFolders = new TreeSet<>();

    @OneToMany(mappedBy = "folder", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("id ASC")
    @LazyCollection(LazyCollectionOption.FALSE)
    @SortNatural
    private SortedSet<File> folderFiles = new TreeSet<>();

    public Folder(){}

    @Override
    public void DeleteAndCleanup()
    {
        for(var file : folderFiles)
            file.DeleteAndCleanup();

        for(var folder : subFolders)
            folder.DeleteAndCleanup();

        DbManager.getInstance().AddObjectToCleanup(this);
    }

    public Folder(Folder parent, String name, Course owner)
    {
        super(name);
        this.parent = parent;
        this.parentCourse = owner;
    }

    public Folder getRoot()
    {
        if(IsRoot())
            return this;

        return parent.getRoot();
    }

    public boolean IsRoot()
    {
        return parent == null;
    }

    public boolean IsLeaf() {return subFolders == null || subFolders.size() == 0; }

    public SortedSet<Folder> getSubFolders()
    {
        return subFolders;
    }

    public void setSubFolders(SortedSet<Folder> subFolders)
    {
        this.subFolders = subFolders;
    }

    public SortedSet<File> getFolderFiles()
    {
        return folderFiles;
    }

    public void setFolderFiles(SortedSet<File> folderFiles)
    {
        this.folderFiles = folderFiles;
    }

    public Course getParentCourse()
    {
        return parentCourse;
    }

    public void setParentCourse(Course parentCourse)
    {
        this.parentCourse = parentCourse;
    }
}
