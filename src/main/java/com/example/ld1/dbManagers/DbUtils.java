//package ind.benas.kursai;
//
//import ind.benas.kursai.enums.CourseAdminType;
//import ind.benas.kursai.enums.UserRole;
//import ind.benas.kursai.models.Courses;
//import ind.benas.kursai.models.File;
//import ind.benas.kursai.models.Folder;
//import ind.benas.kursai.models.User;
//
//import java.sql.*;
//import java.time.LocalDate;
//import java.util.ArrayList;
//
//public class DbUtils {
//
//    private static Connection ConnectToDb()
//    {
//        Connection con = null;
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            var dbUrl = "jdbc:mysql://localhost/courses";
//            var user = "root";
//            var password = "root";
//            con = DriverManager.getConnection(dbUrl, user, password);
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return con;
//    }
//
//    public static void CreateNewUser(User user)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "INSERT INTO users (username, password, userRole, userOrCompanyName, userOrCompanyNumber) VALUES (?, ?, ?, ?, ?);";
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            statement.setString(1, user.getUsername());
//            statement.setString(2, user.getPassword());
//            statement.setInt(3, user.getUserRole().ordinal());
//            statement.setString(4, user.getUserOrCompanyName());
//            statement.setString(5, user.getUserOrCompanyNumber());
//            statement.execute();
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static User LoginUserCredentials(String username, String password)
//    {
//        User user = null;
//
//        var queryString = "SELECT * FROM users WHERE username=? AND password=?;";
//
//        var con = ConnectToDb();
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            statement.setString(1, username);
//            statement.setString(2, password);
//
//            var result = statement.executeQuery();
//
//            if (result.next())
//            {
//                user = new User();
//                user.setId(result.getInt(1));
//                user.setUsername(result.getString(2));
//                user.setPassword(result.getString(3));
//                user.setUserRole(UserRole.values()[result.getInt(4)]);
//                user.setUserOrCompanyName(result.getString(5));
//                user.setUserOrCompanyNumber(result.getString(6));
//            }
//            result.close();
//
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return user;
//    }
//
//    public static ArrayList<User> GetAllUsers()
//    {
//        var users = new ArrayList<User>();
//
//        var queryString = "SELECT * FROM users;";
//
//        var con = ConnectToDb();
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            var result = statement.executeQuery();
//
//            while (result.next())
//            {
//                var user = new User();
//                user.setId(result.getInt(1));
//                user.setUsername(result.getString(2));
//                user.setPassword(result.getString(3));
//                user.setUserRole(UserRole.values()[result.getInt(4)]);
//                user.setUserOrCompanyName(result.getString(5));
//                user.setUserOrCompanyNumber(result.getString(6));
//                users.add(user);
//            }
//
//            result.close();
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return users;
//    }
//
//    public static void DeleteUser(int id)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "DELETE FROM users WHERE id=?";
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, id);
//            statement.execute();
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static User GetUserById(int userId)
//    {
//        var con = ConnectToDb();
//
//        User user = null;
//
//        var queryString = "SELECT * FROM users WHERE id=?";
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, userId);
//
//            var result = statement.executeQuery();
//
//            if(result.next())
//            {
//                user = new User();
//                user.setId(result.getInt(1));
//                user.setUsername(result.getString(2));
//                user.setPassword(result.getString(3));
//                user.setUserRole(UserRole.values()[result.getInt(4)]);
//                user.setUserOrCompanyName(result.getString(5));
//                user.setUserOrCompanyNumber(result.getString(6));
//            }
//
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return user;
//    }
//
//    public static ArrayList<User> GetAllCourseModerators(int courseId)
//    {
//        var con = ConnectToDb();
//        var moderators = new ArrayList<User>();
//
//        var queryString = "SELECT userId, type FROM courseAdmin WHERE courseId=?";
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, courseId);
//
//            var result = statement.executeQuery();
//
//            while (result.next())
//            {
//                var type = CourseAdminType.values()[result.getInt(2)];
//
//                if(type == CourseAdminType.MODERATOR)
//                {
//                    var moderator = GetUserById(result.getInt(1));
//
//                    if(moderator != null)
//                        moderators.add(moderator);
//                }
//            }
//
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return moderators;
//    }
//
//    public static ArrayList<User> GetAllCourseStudents(int courseId)
//    {
//        var con = ConnectToDb();
//        var students = new ArrayList<User>();
//
//        var queryString = "SELECT userId, type FROM courseAdmin WHERE courseId=?";
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, courseId);
//
//            var result = statement.executeQuery();
//
//            while (result.next())
//            {
//                var type = CourseAdminType.values()[result.getInt(2)];
//
//                if(type == CourseAdminType.STUDENT)
//                {
//                    var student = GetUserById(result.getInt(1));
//
//                    if(student != null)
//                        students.add(student);
//                }
//            }
//
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return students;
//    }
//
//    public static ArrayList<Courses> GetAllUserCourses(int userId)
//    {
//        var con = ConnectToDb();
//
//        var courses = new ArrayList<Courses>();
//
//        var queryString = "SELECT * FROM course WHERE creatorUserId=?";
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, userId);
//
//            var result = statement.executeQuery();
//
//            while (result.next())
//            {
//                var course = new Courses();
//                course.setId(result.getInt(1));
//                course.setTitle(result.getString(2));
//                course.setDescription(result.getString(3));
//                course.setDateCreated(result.getDate(4).toLocalDate());
//                course.setDateModified(result.getDate(5).toLocalDate());
//                course.setPrice(result.getDouble(6));
//                course.setCreatorUserId(result.getInt(7));
//                course.setModerators(GetAllCourseModerators(course.getId()));
//                course.setStudents(GetAllCourseStudents(course.getId()));
//                courses.add(course);
//            }
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return courses;
//    }
//
//    public static void UpdateCourse(int courseId, String title, String description, double price)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "UPDATE course SET title=?, description=?, price=?, dateModified=? WHERE id=? ORDER BY dateCreated ASC";
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            statement.setString(1, title);
//            statement.setString(2, description);
//            statement.setDouble(3, price);
//            statement.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
//            statement.setInt(5, courseId);
//            statement.execute();
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static void CreateNewCourse(String title, String description, double price, int userId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "INSERT INTO course (title, description, price, dateCreated, dateModified, creatorUserId) VALUES (?, ?, ?, ?, ?, ?)";
//
//        try
//        {
//            var statement = con.prepareStatement(queryString);
//            statement.setString(1, title);
//            statement.setString(2, description);
//            statement.setDouble(3, price);
//            statement.setDate(4, Date.valueOf(LocalDate.now()));
//            statement.setDate(5, Date.valueOf(LocalDate.now()));
//            statement.setInt(6, userId);
//            statement.execute();
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static void DeleteCourse(int courseId) {
//        var con = ConnectToDb();
//
//        var queryString = "DELETE FROM course WHERE id=?";
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, courseId);
//            statement.execute();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static ArrayList<Folder> GetAllFoldersByCourseId(int courseId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "SELECT * FROM folder WHERE courseId=?;";
//
//        var result = new ArrayList<Folder>();
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, courseId);
//
//            var results = statement.executeQuery();
//
//            while (results.next())
//            {
//                var folder = new Folder();
//                folder.setId(results.getInt(1));
//                folder.setName(results.getString(2));
//                folder.setDateAdded(results.getDate(3).toLocalDate());
//                folder.setCreatedByUserId(results.getInt(4));
//                folder.setParentFolderId(results.getInt(5));
//                folder.setCourseId(results.getInt(6));
//                result.add(folder);
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return result;
//    }
//
//    public static ArrayList<Folder> GetAllFoldersFolderId(int folderId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "SELECT * FROM folder WHERE parentFolderId=?;";
//
//        var result = new ArrayList<Folder>();
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, folderId);
//
//            var results = statement.executeQuery();
//
//            while (results.next())
//            {
//                var folder = new Folder();
//                folder.setId(results.getInt(1));
//                folder.setName(results.getString(2));
//                folder.setDateAdded(results.getDate(3).toLocalDate());
//                folder.setCreatedByUserId(results.getInt(4));
//                folder.setParentFolderId(results.getInt(5));
//                folder.setCourseId(results.getInt(6));
//                result.add(folder);
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return result;
//    }
//
//    public static void CreateNewFolder(String name, int userId, int parentFolderId, int courseId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "";
//        if(parentFolderId != -1)
//        {
//            queryString = "INSERT INTO folder (name, dateAdded, createdByUserId, parentFolderId) VALUES (?, ?, ?, ?)";
//        } else
//            queryString = "INSERT INTO folder (name, dateAdded, createdByUserId, courseId) VALUES (?, ?, ?, ?)";
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setString(1, name);
//            statement.setDate(2, Date.valueOf(LocalDate.now()));
//            statement.setInt(3, userId);
//
//            if(parentFolderId != -1)
//                statement.setInt(4, parentFolderId);
//            else
//                statement.setInt(4, courseId);
//
//            statement.execute();
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static void DeleteFolder(int folderId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "DELETE FROM folder WHERE id=?;";
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, folderId);
//            statement.execute();
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static ArrayList<File> GetAllFiles(int folderId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "SELECT * FROM file WHERE folderId=?;";
//
//        var ats = new ArrayList<File>();
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, folderId);
//            var result = statement.executeQuery();
//
//            while (result.next())
//            {
//                var file = new File();
//                file.setId(result.getInt(1));
//                file.setName(result.getString(2));
//                file.setData(result.getString(3));
//                file.setDateAdded(result.getDate(4).toLocalDate());
//                file.setUploadedByUserId(result.getInt(5));
//                file.setFolderId(result.getInt(6));
//
//                ats.add(file);
//            }
//
//            disconnectFromDb(con,statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return ats;
//    }
//
//    public static void CreateFile(String name, String data, int userid, int folderId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "INSERT INTO file (name, data, dateAdded, uploadedByUserId, folderId) VALUES (?, ?, ?, ?, ?);";
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setString(1, name);
//            statement.setString(2, data);
//            statement.setDate(3, Date.valueOf(LocalDate.now()));
//            statement.setInt(4, userid);
//            statement.setInt(5, folderId);
//            statement.execute();
//            disconnectFromDb(con, statement);
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static void DeleteFile(int fileId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "DELETE FROM file WHERE id=?;";
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, fileId);
//            statement.execute();
//            disconnectFromDb(con, statement);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static Folder GetFolderById(int folderId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "SELECT * FROM folder WHERE id=?;";
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, folderId);
//
//            var result = statement.executeQuery();
//
//            if(result.next())
//            {
//                var folder = new Folder();
//                folder.setId(result.getInt(1));
//                folder.setName(result.getString(2));
//                folder.setDateAdded(result.getDate(3).toLocalDate());
//                folder.setCreatedByUserId(result.getInt(4));
//                folder.setParentFolderId(result.getInt(5));
//                folder.setCourseId(result.getInt(6));
//                disconnectFromDb(con, statement);
//                return folder;
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return null;
//    }
//
//    public static Courses GetCourseById(int courseId)
//    {
//        var con = ConnectToDb();
//
//        var queryString = "SELECT * FROM course WHERE id=?;";
//
//        try {
//            var statement = con.prepareStatement(queryString);
//            statement.setInt(1, courseId);
//
//            var result = statement.executeQuery();
//
//            if(result.next())
//            {
//                var course = new Courses();
//                course.setId(result.getInt(1));
//                course.setTitle(result.getString(2));
//                course.setDescription(result.getString(3));
//                course.setDateCreated(result.getDate(4).toLocalDate());
//                course.setDateModified(result.getDate(5).toLocalDate());
//                course.setPrice(result.getDouble(6));
//                course.setCreatorUserId(result.getInt(7));
//                course.setModerators(GetAllCourseModerators(course.getId()));
//                course.setStudents(GetAllCourseStudents(course.getId()));
//                return course;
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private static void disconnectFromDb(Connection connection, Statement statement) {
//
//        try {
//            if (connection != null && statement != null) {
//                connection.close();
//                statement.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
