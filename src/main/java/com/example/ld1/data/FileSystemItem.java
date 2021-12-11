package com.example.ld1.data;

public interface FileSystemItem
{
    public String getDisplayName();
    public void setDisplayName(String name);
    public boolean isFolder();
    public boolean isFile();

    public <T> T as(Class<T> t);
}
