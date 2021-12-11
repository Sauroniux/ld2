package com.example.ld1.dbManagers;

import com.example.ld1.data.Course;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDbManager extends DbManagerBase
{
    private static CourseDbManager instance;

    private CourseDbManager(){}

    public static CourseDbManager getInstance()
    {
        {
            if(instance == null)
                instance = new CourseDbManager();

            return instance;
        }
    }

    public int CreateCourse(Course course)
    {
        int id = -1;
        var con = ConnectToDb();

        String queryString = "INSERT INTO course (title, description, root_folder_id) VALUES(?, ?, ?)";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, course.getTitle());
            statement.setString(2, course.getDescription());
            statement.setInt(3, course.getRootFolderId());

            var result = statement.execute();

            id = GetLatestIdFromDb(con);

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        course.setId(id);
        return id;
    }

    public List<Course> GetAllCourses()
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM course";

        List<Course> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);

            var result = statement.executeQuery();

            while (result.next())
                list.add(constructCourse(result));

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public Course GetCourseById(int id)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM course WHERE id = ?";

        Course item = null;

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);
            var result = statement.executeQuery();

            if (result.next())
                item = constructCourse(result);

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return item;
    }

    public void UpdateCourse(Course course)
    {
        var con = ConnectToDb();
        String queryString = "UPDATE course SET title = ?, description = ?, root_folder_id = ? WHERE id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, course.getTitle());
            statement.setString(2, course.getDescription());
            statement.setInt(3, course.getRootFolderId());

            var result = statement.execute();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public void DeleteCourse(int id)
    {
        var con = ConnectToDb();
        String queryString = "DELETE FROM course WHERE id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);

            var result = statement.execute();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }
}
