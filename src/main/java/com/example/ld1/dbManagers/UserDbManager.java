package com.example.ld1.dbManagers;

import com.example.ld1.data.AccountType;
import com.example.ld1.data.User;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDbManager extends DbManagerBase
{
    private static UserDbManager instance;
    private User currentUser;

    private UserDbManager(){}

    public static UserDbManager getInstance()
    {
        {
            if(instance == null)
                instance = new UserDbManager();

            return instance;
        }
    }

    public User getCurrentUser()
    {
        return currentUser;
    }
    public void setCurrentUser(User user) {currentUser = user;}

    public int CreateUser(User user)
    {
        int id = -1;
        var con = ConnectToDb();
        String queryString = "INSERT INTO user (username, password, date_created, date_modified, secondary_text_1, secondary_text_2, account_type) VALUES(?, ?, ?, ?, ?, ?, ?)";
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

            id = GetLatestIdFromDb(con);

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

            var result = statement.execute();
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

            var result = statement.execute();
            disconnectFromDb(con, statement);
        }
        catch (SQLException e) {e.printStackTrace();}
    }
}
