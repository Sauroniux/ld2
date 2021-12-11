package com.example.ld1.data;



public class File  extends dbBase implements FileSystemItem
{
    private int folderId;
    private String name;
    private String content;

    public File(int folderId, String name, String content)
    {
        id = -1;
        this.folderId = folderId;
        this.name = name;
        this.content = content;
    }

    public <T> T as(Class<T> t) {
        return t.isInstance(this) ? t.cast(this) : null;
    }

    public String getFileExtension()
    {
        int lastIndex = getName().lastIndexOf('.');
        if(lastIndex == -1)
            return "";
        return getName().substring(lastIndex, getName().length() - 1);
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
        return false;
    }

    @Override
    public boolean isFile()
    {
        return true;
    }

    public int getFolderId()
    {
        return folderId;
    }

    public void setFolderId(int folderId)
    {
        this.folderId = folderId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
