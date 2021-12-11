package com.example.ld1.data;

import com.example.ld1.dbManagers.DbManager2;

public class Course extends dbBase
{
    private String title;
    private String description;
    private int rootFolderId;

    public Course(){}

    public Course(String title, String description)
    {
        this.title = title;
        this.description = description;
        this.rootFolderId = -1;
    }

    public Course(String title, String description, int rootFolderId)
    {
        this.title = title;
        this.description = description;
        this.rootFolderId = rootFolderId;
    }

    public void AppendModerator(User user)
    {
        DbManager2.getInstance().AddCourseModerator(user, this);
    }

    public void AppendViewer(User user)
    {
        DbManager2.getInstance().AddCourseViewer(user, this);
    }

    public void RemoveModerator(User user)
    {
        DbManager2.getInstance().RemoveCourseModerator(user, this);
    }

    public void RemoveViewer(User user)
    {
        DbManager2.getInstance().RemoveCourseViewer(user, this);
    }

    public boolean createRootFolder()
    {
        Folder rootFolder = new Folder("_root_" + id);
        var folderId = DbManager2.getInstance().CreateFolder(rootFolder);

        if(folderId == -1)
        {
            return false;
        }

        rootFolderId = folderId;

        DbManager2.getInstance().UpdateCourse(this);

        return true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRootFolderId() {
        return rootFolderId;
    }

    public void setRootFolderId(int rootFolderId) {
        this.rootFolderId = rootFolderId;
    }
}
