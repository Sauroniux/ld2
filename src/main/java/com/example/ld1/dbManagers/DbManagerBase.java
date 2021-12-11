package com.example.ld1.dbManagers;

import com.example.ld1.data.*;

import java.sql.*;
import java.time.LocalDate;

public abstract class DbManagerBase
{
    protected static Connection ConnectToDb()
    {
        Connection con = null;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            var dbUrl = "jdbc:mysql://localhost/courses_3";
            var user = "root";
            var password = "root";
            con = DriverManager.getConnection(dbUrl, user, password);

        }
        catch (SQLException e) {e.printStackTrace();}
        catch (ClassNotFoundException e) {e.printStackTrace();}

        return con;
    }

    protected static void disconnectFromDb(Connection connection, Statement statement)
    {
        try
        {
            if (connection != null && statement != null)
            {
                connection.close();
                statement.close();
            }
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    protected LocalDate dateToLocalDate(Date dateToConvert)
    {
        return dateToConvert.toLocalDate();
    }

    protected int GetLatestIdFromDb(Connection con)
    {
        try
        {
            String queryString = "SELECT LAST_INSERT_ID()";
            var statement = con.prepareStatement(queryString);

            var result = statement.executeQuery();

            if(result.next())
                return result.getInt(1);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return -1;
    }

    protected User constructUser(ResultSet result)
    {
        User user = null;
        try
        {
            user = new User(
                    result.getString(2),
                    result.getString(3),
                    dateToLocalDate(result.getDate(4)),
                    dateToLocalDate(result.getDate(5)),
                    result.getString(6),
                    result.getString(7),
                    AccountType.values()[result.getInt(8)]
            );

            user.setId(result.getInt(1));
        }
        catch (SQLException e) { e.printStackTrace();}

        return user;
    }

    protected Course constructCourse(ResultSet result)
    {
        Course course = null;
        try
        {
            course = new Course(
                    result.getString(2),
                    result.getString(3),
                    result.getInt(4)
            );
            course.setId(result.getInt(1));
        }
        catch (SQLException e) { e.printStackTrace();}

        return course;
    }

    protected Folder constructFolder(ResultSet result)
    {
        Folder folder = null;
        try
        {
            folder = new Folder(
                    result.getString(2),
                    result.getInt(3)
            );
            folder.setId(result.getInt(1));
        }
        catch (SQLException e) { e.printStackTrace();}

        return folder;
    }

    protected File constructFile(ResultSet result)
    {
        File file = null;
        try
        {
            file = new File(
                    result.getInt(2),
                    result.getString(3),
                    result.getString(4)

            );
            file.setId(result.getInt(1));
        }
        catch (SQLException e) { e.printStackTrace();}

        return file;
    }
}
