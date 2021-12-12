package com.example.ld1.data;

import com.example.ld1.dbManagers.RelationshipDbManager;

import java.util.List;

public class Folder extends dbBase implements FileSystemItem
{
    private String name;
    private int parentId;

    public Folder(){}

    public Folder(String name)
    {
        this.name = name;
        this.parentId = -1;
    }

    public Folder(String name, int parentId)
    {
        this.name = name;
        this.parentId = parentId;
    }

    public <T> T as(Class<T> t) {
        return t.isInstance(this) ? t.cast(this) : null;
    }

    public boolean isLeaf()
    {
        var children = RelationshipDbManager.getInstance().GetChildFolders(this);

        return children == null || children.size() == 0;
    }

    public List<File> getFolderFiles()
    {
        return RelationshipDbManager.getInstance().GetChildFiles(this);
    }

    public List<Folder> getSubFolders()
    {
        return RelationshipDbManager.getInstance().GetChildFolders(this);
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
    public String getDisplayName()
    {
        return name;
    }

    @Override
    public void setDisplayName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean isFolder()
    {
        return true;
    }

    @Override
    public boolean isFile()
    {
        return false;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }
}
