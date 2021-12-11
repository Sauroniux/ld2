package com.example.ld1.dbManagers;

import com.example.ld1.data.Folder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FolderDbManager extends DbManagerBase
{
    private static FolderDbManager instance;

    private FolderDbManager(){}

    public static FolderDbManager getInstance()
    {
        {
            if(instance == null)
                instance = new FolderDbManager();

            return instance;
        }
    }

    public int CreateFolder(Folder folder)
    {
        int id = -1;
        var con = ConnectToDb();

        String queryString = "INSERT INTO folder (name, parent_id) VALUES(?, ?)";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setString(1, folder.getName());
            var parentId = folder.getParentId();
            if(parentId <= 0)
                statement.setNull(2, java.sql.Types.INTEGER);
            else
                statement.setInt(2, parentId);

            var result = statement.execute();

            id = GetLatestIdFromDb(con);

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

            var result = statement.execute();
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

            var result = statement.execute();
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
}
