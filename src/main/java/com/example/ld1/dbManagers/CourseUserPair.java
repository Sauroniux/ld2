package com.example.ld1.dbManagers;

import com.example.ld1.data.Course;
import com.example.ld1.data.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseUserPair
{
    private int userId;
    private int courseId;

    public CourseUserPair(ResultSet result)
    {
        try
        {
            this.userId = result.getInt(1);
            this.courseId = result.getInt(2);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public CourseUserPair(int userId, int courseId)
    {
        this.userId = userId;
        this.courseId = courseId;
    }

    public static List<Course> fetchCourses(List<CourseUserPair> pairList)
    {
        if(pairList == null)
            return null;

        List<Course> list = new ArrayList<>();

        for(var pair : pairList)
            list.add(pair.getCourse());

        return list;
    }

    public static List<User> fetchUsers(List<CourseUserPair> pairList)
    {
        if(pairList == null)
            return null;

        List<User> list = new ArrayList<>();

        for(var pair : pairList)
            list.add(pair.getUser());

        return list;
    }

    public static List<Integer> fetchCoursesIds(List<CourseUserPair> pairList)
    {
        if(pairList == null)
            return null;

        List<Integer> list = new ArrayList<>();

        for(var pair : pairList)
            list.add(pair.courseId);

        return list;
    }

    public static List<Integer> fetchUserIds(List<CourseUserPair> pairList)
    {
        if(pairList == null)
            return null;

        List<Integer> list = new ArrayList<>();

        for(var pair : pairList)
            list.add(pair.userId);

        return list;
    }

    public User getUser()
    {
        return UserDbManager.getInstance().GetUserById(userId);
    }

    public Course getCourse()
    {
        return CourseDbManager.getInstance().GetCourseById(courseId);
    }
}
