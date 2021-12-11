package com.example.ld1.dbManagers;

import com.example.ld1.data.*;
import com.example.ld2.webControllers.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DbManager2
{
    private static DbManager2 instance;

    private User currentUser;

    private DbManager2()
    {
        super();
        currentUser = null;
    }

    public User getCurrentUser()
    {
        return currentUser;
    }
    public void setCurrentUser(User user) {currentUser = user;}

    public static DbManager2 getInstance()
    {
        if(instance == null)
            instance = new DbManager2();

        return instance;
    }

    private static Connection ConnectToDb()
    {
        Connection con = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            var dbUrl = "jdbc:mysql://localhost/courses";
            var user = "root";
            var password = "root";
            con = DriverManager.getConnection(dbUrl, user, password);

        }
        catch (SQLException e) {e.printStackTrace();}
        catch (ClassNotFoundException e) {e.printStackTrace();}

        return con;
    }

    private static void disconnectFromDb(Connection connection, Statement statement)
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

    private LocalDate dateToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    //region User

    public int CreateUser(User user)
    {
        int id = -1;
        var con = ConnectToDb();
        String queryString = "INSERT INTO user (username, password, date_created, date_modified, secondary_text1, secondary_text2, account_type) VALUES(?, ?, ?, ?, ?, ?, ?); SELECT LAST_INSERT_ID();";
        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setDate(3, Date.valueOf(user.getDateCreated()));
            statement.setDate(4, Date.valueOf(user.getDateModified()));
            statement.setString(5, user.getSecondaryText1());
            statement.setString(6, user.getSecondaryText2());
            statement.setInt(7, user.getAccountType().getValue());

            var result = statement.execute();

            if (result.next())
                id = result.getInt(1);

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        user.setId(id);
        return id;
    }

    public List<User> GetAllUsers()
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM user";

        List<User> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);

            var result = statement.executeQuery();

            while (result.next())
                list.add(constructUser(result));

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public User GetUserById(int id)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM user WHERE id = ?";

        User item = null;

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);
            var result = statement.executeQuery();

            if (result.next())
                item = constructUser(result);

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return item;
    }

    public User GetUser(int id)
    {
        var con = ConnectToDb();
        var queryString = "SELECT * FROM user WHERE id=?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            User user = null;

            if (result.next())
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
            result.close();

            disconnectFromDb(con, statement);

            return user;
        }
        catch (SQLException e) {e.printStackTrace();}

        return null;
    }

    public User LoginUser(String username, String password)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM user WHERE username=? AND password=?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, username);
            statement.setString(2, password);

            var result = statement.executeQuery();

            User user = null;

            if (result.next())
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
            result.close();

            disconnectFromDb(con, statement);

            return user;
        }
        catch (SQLException e) {e.printStackTrace();}

        return null;
    }

    public User GetByUsername(String username)
    {
        User user = null;
        var con = ConnectToDb();
        String queryString = "SELECT * FROM user WHERE username=?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, username);

            var result = statement.executeQuery();

            if (result.next())
            {
                user = constructUser(result);
            }
            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return user;
    }

    public boolean doesUsernameExist(String username)
    {
        boolean found = false;
        var con = ConnectToDb();
        String queryString = "SELECT * FROM user WHERE username=?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, username);

            var result = statement.executeQuery();

            User user = null;

            if (result.next())
            {
                found = true;
            }
            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return found;
    }

    public void UpdateUser(User user)
    {
        var con = ConnectToDb();
        String queryString = "UPDATE user SET username = ?, password = ?, date_created = ?, date_modified = ?, secondary_text1 = ?, secondary_text2 = ? WHERE id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setDate(3, Date.valueOf(user.getDateCreated()));
            statement.setDate(4, Date.valueOf(user.getDateModified()));
            statement.setString(5, user.getSecondaryText1());
            statement.setString(6, user.getSecondaryText2());

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public void DeleteUser(int id)
    {
        var con = ConnectToDb();
        String queryString = "DELETE FROM user WHERE id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    //endregion
    //region Course

    public int CreateCourse(Course course)
    {
        int id = -1;
        var con = ConnectToDb();

        String queryString = "INSERT INTO course (title, description, root_folder_id) VALUES(?, ?, ?); SELECT LAST_INSERT_ID();";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, course.getTitle());
            statement.setString(2, course.getDescription());
            statement.setInt(3, course.getRootFolderId());

            var result = statement.executeQuery();

            if(result.next())
                id = result.getInt(1);

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

            var result = statement.executeQuery();
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

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    //endregion
    //region Folder ==================================================================================================

    public int CreateFolder(Folder folder)
    {
        int id = -1;
        var con = ConnectToDb();

        String queryString = "INSERT INTO folder (name, parent_id) VALUES(?, ?); SELECT LAST_INSERT_ID();";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, folder.getName());
            statement.setInt(2, folder.getParentId());

            var result = statement.executeQuery();

            if(result.next())
                id = result.getInt(1);

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        folder.setId(id);
        return id;
    }

    public List<Folder> GetAllFolders()
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM folder";

        List<Folder> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);

            var result = statement.executeQuery();

            while (result.next())
                list.add(constructFolder(result));

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public Folder GetFolderById(int id)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM folder WHERE id = ?";

        Folder item = null;

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);
            var result = statement.executeQuery();

            if (result.next())
                item = constructFolder(result);

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return item;
    }

    public void DeleteFolder(int id)
    {
        var con = ConnectToDb();
        String queryString = "DELETE FROM folder WHERE id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public void UpdateFolder(Folder folder)
    {
        var con = ConnectToDb();
        String queryString = "UPDATE folder SET name = ?, parent_id = ? WHERE id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, folder.getName());
            statement.setInt(2, folder.getParentId());
            statement.setInt(3, folder.getId());

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public Folder GetFolder(int id)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM folder WHERE id = ?";

        Folder folder = null;

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            if (result.next())
            {
                folder = new Folder(
                        result.getString(2),
                        result.getInt(3)
                );
                folder.setId(result.getInt(1));
            }
            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return folder;
    }
    //endregion
    //region File ==================================================================================================

    public int CreateFile(File file)
    {
        int id = -1;
        var con = ConnectToDb();

        String queryString = "INSERT INTO file (folder_id, name, content) VALUES(?, ?, ?); SELECT LAST_INSERT_ID();";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, file.getFolderId());
            statement.setString(2, file.getName());
            statement.setString(2, file.getContent());

            var result = statement.executeQuery();

            if(result.next())
                id = result.getInt(1);

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        file.setId(id);
        return id;
    }

    public List<File> GetAllFiles()
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM file";

        List<File> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);

            var result = statement.executeQuery();

            while (result.next())
                list.add(constructFile(result));

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public File GetFileById(int id)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM folder WHERE id = ?";

        File item = null;

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);
            var result = statement.executeQuery();

            if (result.next())
                item = constructFile(result);

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return item;
    }

    public void DeleteFile(int id)
    {
        var con = ConnectToDb();
        String queryString = "DELETE FROM file WHERE id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public void UpdateFile(File file)
    {
        var con = ConnectToDb();
        String queryString = "UPDATE file SET folder_id = ?, name = ?, content = ? WHERE id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, file.getFolderId());
            statement.setString(2, file.getName());
            statement.setString(3, file.getContent());
            statement.setInt(4, file.getId());

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public File GetFile(int id)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM file WHERE id = ?";

        File file = null;

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            if (result.next())
            {
                file = new File(
                        result.getInt(2),
                        result.getString(3),
                        result.getString(4)
                );
                file.setId(result.getInt(1));
            }
            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return file;
    }

    //endregion
    //region Relationships

    public void AddCourseModerator(User moderator, Course course)
    {
        var con = ConnectToDb();
        String queryString = "INSERT INTO course_moderators (user_id, course_id) VALUES(?, ?)";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, moderator.getId());
            statement.setInt(2, course.getId());

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public List<User> GetCourseModerators(Course course)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM course_moderators WHERE course_id = ?";

        List<User> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, course.getId());

            var result = statement.executeQuery();

            User user = null;

            while (result.next())
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

                list.add(user);
            }
            result.close();

            disconnectFromDb(con, statement);

            return list;
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public List<Course> GetModeratedCourses(User user)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM course_moderators WHERE user_id = ?";

        List<Course> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, user.getId());

            var result = statement.executeQuery();

            Course course = null;

            while (result.next())
            {
                course = new Course(
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4)
                );
                course.setId(result.getInt(1));

                list.add(course);
            }
            result.close();

            disconnectFromDb(con, statement);

            return list;
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public void RemoveCourseModerator(User moderator, Course course)
    {
        var con = ConnectToDb();
        String queryString = "DELETE FROM course_moderators WHERE user_id = ? AND course_id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, moderator.getId());
            statement.setInt(2, course.getId());

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public void AddCourseViewer(User viewer, Course course)
    {
        var con = ConnectToDb();
        String queryString = "INSERT INTO course_viewers (user_id, course_id) VALUES(?, ?)";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, viewer.getId());
            statement.setInt(2, course.getId());

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public List<Course> GetViewedCourses(User user)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM course_viewers WHERE user_id = ?";

        List<Course> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, user.getId());

            var result = statement.executeQuery();

            Course course = null;

            while (result.next())
            {
                course = new Course(
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4)
                );
                course.setId(result.getInt(1));

                list.add(course);
            }
            result.close();

            disconnectFromDb(con, statement);

            return list;
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public List<User> GetCourseViewers(Course course)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM course_viewers WHERE course_id = ?";

        List<User> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, course.getId());

            var result = statement.executeQuery();

            User user = null;

            while (result.next())
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

                list.add(user);
            }
            result.close();

            disconnectFromDb(con, statement);

            return list;
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public void RemoveCourseViewer(User viewer, Course course)
    {
        var con = ConnectToDb();
        String queryString = "DELETE FROM course_viewers WHERE user_id = ? AND course_id = ?";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, viewer.getId());
            statement.setInt(2, course.getId());

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public List<Folder> GetChildFolders(Folder folder)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM folder WHERE parent_id = ?";

        List<Folder> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, folder.getId());

            var result = statement.executeQuery();

            while (result.next())
            {
                Folder retrievedFolder = new Folder(
                        result.getString(2),
                        result.getInt(3)

                );
                retrievedFolder.setId(result.getInt(1));

                list.add(retrievedFolder);
            }
            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public List<File> GetChildFiles(Folder folder)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM folder WHERE parent_id = ?";

        List<File> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, folder.getId());

            var result = statement.executeQuery();

            while (result.next())
            {
                File retrievedFile = constructFile(result);

                list.add(retrievedFile);
            }
            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public List<File> GetChileFiles(Folder folder)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM file WHERE folder_id = ?";

        List<File> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, folder.getId());

            var result = statement.executeQuery();

            while (result.next())
            {
                File retrievedFile = new File(
                        result.getInt(2),
                        result.getString(3),
                        result.getString(4)

                );
                retrievedFile.setId(result.getInt(1));

                list.add(retrievedFile);
            }
            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    public void AddCourseOwner(User owner, Course course)
    {
        var con = ConnectToDb();
        String queryString = "INSERT INTO course_owners (user_id, course_id) VALUES(?, ?)";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, owner.getId());
            statement.setInt(2, course.getId());

            var result = statement.executeQuery();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public List<Course> GetOwnedCourses(User user)
    {
        var con = ConnectToDb();
        String queryString = "SELECT * FROM course_owners WHERE user_id = ?";

        List<Course> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, user.getId());

            var result = statement.executeQuery();

            Course course = null;

            while (result.next())
            {
                course = new Course(
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4)
                );
                course.setId(result.getInt(1));

                list.add(course);
            }
            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }

    //endregion
    //region Misc
    public User constructUser(ResultSet result)
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

    public Course constructCourse(ResultSet result)
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

    public Folder constructFolder(ResultSet result)
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

    public File constructFile(ResultSet result)
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
    //endregion
}
