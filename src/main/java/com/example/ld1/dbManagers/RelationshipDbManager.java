package com.example.ld1.dbManagers;

import com.example.ld1.data.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelationshipDbManager extends DbManagerBase
{
    private static RelationshipDbManager instance;

    private RelationshipDbManager(){}

    public static RelationshipDbManager getInstance()
    {
        {
            if(instance == null)
                instance = new RelationshipDbManager();

            return instance;
        }
    }

    public void AddCourseModerator(User moderator, Course course)
    {
        var con = ConnectToDb();
        String queryString = "INSERT INTO course_moderators (user_id, course_id) VALUES(?, ?)";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, moderator.getId());
            statement.setInt(2, course.getId());

            var result = statement.execute();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public List<User> GetCourseModeratorsObjects(Course course)
    {
        return CourseUserPair.fetchUsers(GetCourseModerators(course));
    }

    public List<Integer> GetCourseModeratorsIds(Course course)
    {
        return CourseUserPair.fetchUserIds(GetCourseModerators(course));
    }

    public List<CourseUserPair> GetCourseModerators(Course course)
    {
        String queryString = "SELECT * FROM course_moderators WHERE course_id = ?";
        return getPairsWithOneId(queryString, course.getId());
    }

    public List<Course> GetModeratedCoursesObjects(User user)
    {
        return CourseUserPair.fetchCourses(GetModeratedCourses(user));
    }

    public List<Integer> GetModeratedCoursesIds(User user)
    {
        return CourseUserPair.fetchCoursesIds(GetModeratedCourses(user));
    }

    public List<CourseUserPair> GetModeratedCourses(User user)
    {
        String queryString = "SELECT * FROM course_moderators WHERE user_id = ?";
        return getPairsWithOneId(queryString, user.getId());
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

            var result = statement.execute();
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

            var result = statement.execute();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public List<Course> GetViewedCoursesObjects(User user)
    {
        return CourseUserPair.fetchCourses(GetViewedCourses(user));
    }

    public List<Integer> GetViewedCoursesIds(User user)
    {
        return CourseUserPair.fetchCoursesIds(GetViewedCourses(user));
    }

    public List<CourseUserPair> GetViewedCourses(User user)
    {
        String queryString = "SELECT * FROM course_viewers WHERE user_id = ?";
        return getPairsWithOneId(queryString, user.getId());
    }

    public List<User> GetCourseViewersObjects(Course course)
    {
        return CourseUserPair.fetchUsers(GetCourseViewers(course));
    }

    public List<Integer> GetCourseViewersIds(Course course)
    {
        return CourseUserPair.fetchUserIds(GetCourseViewers(course));
    }

    public List<CourseUserPair> GetCourseViewers(Course course)
    {
        String queryString = "SELECT * FROM course_viewers WHERE course_id = ?";
        return getPairsWithOneId(queryString, course.getId());
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

            var result = statement.execute();
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
        String queryString = "SELECT * FROM file WHERE folder_id = ?";

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

            var result = statement.execute();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    public List<CourseUserPair> GetOwnedCoursesPairs(User user)
    {
        String queryString = "SELECT * FROM course_owners WHERE user_id = ?";
        return getPairsWithOneId(queryString, user.getId());
    }

    public List<Integer> GetOwnedCoursesIds(User user)
    {
        return CourseUserPair.fetchCoursesIds(GetOwnedCoursesPairs(user));
    }

    public List<Course> GetOwnedCourses(User user)
    {
        return CourseUserPair.fetchCourses(GetOwnedCoursesPairs(user));
    }

    private List<CourseUserPair> getPairsWithOneId(String queryString, int id)
    {
        var con = ConnectToDb();
        List<CourseUserPair> list = new ArrayList<>();

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            while (result.next())
                list.add(new CourseUserPair(result));

            result.close();

            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}

        return list;
    }
}
