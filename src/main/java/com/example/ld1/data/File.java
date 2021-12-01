package com.example.ld1.data;

import com.example.ld1.dbManagers.DbManager;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class File extends FileSystemItem implements Serializable
{
    private String content;

    @ManyToOne
    private Folder folder;

    public File(){}

    @Override
    public void DeleteAndCleanup()
    {
        DbManager.getInstance().AddObjectToCleanup(this);
    }

    public File(Folder parent, String name, String content)
    {
        super(name);
        this.content = content;
        this.folder = parent;
    }

    public String getFileExtension()
    {
        int lastIndex = getName().lastIndexOf('.');
        if(lastIndex == -1)
            return "";
        return getName().substring(lastIndex, getName().length() - 1);
    }

    public Folder getFolder()
    {
        return folder;
    }

    public void setFolder(Folder folder)
    {
        this.folder = folder;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
