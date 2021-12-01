package com.example.ld1.data;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class FileSystemItem extends dbBase implements Comparable<FileSystemItem>
{
    private String name;

    public FileSystemItem(){}

    public FileSystemItem(String name)
    {
        this.name = name;
    }

    public boolean isFolder()
    {
        Folder result = as(Folder.class, this);

        return result != null;
    }

    public boolean isFile()
    {
        File result = as(File.class, this);

        return result != null;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int compareTo(FileSystemItem o)
    {
        return name.compareTo(name);
    }

    public Company getOwner()
    {
        if(this.isFolder())
        {
            Folder root = this.as(Folder.class).getRoot();
            return root.getParentCourse().getCreator();
        }

        Folder folder = this.as(File.class).getFolder();
        return folder.getRoot().getParentCourse().getCreator();
    }

    public abstract void DeleteAndCleanup();
}
