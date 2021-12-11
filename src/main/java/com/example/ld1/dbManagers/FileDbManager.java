package com.example.ld1.dbManagers;

import com.example.ld1.data.File;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileDbManager extends DbManagerBase
{
    private static FileDbManager instance;

    private FileDbManager(){}

    public static FileDbManager getInstance()
    {
        {
            if(instance == null)
                instance = new FileDbManager();

            return instance;
        }
    }

    public int CreateFile(File file)
    {
        int id = -1;
        var con = ConnectToDb();

        String queryString = "INSERT INTO file (folder_id, name, content) VALUES(?, ?, ?)";

        try
        {
            var statement = con.prepareStatement(queryString);
            statement.setInt(1, file.getFolderId());
            statement.setString(2, file.getName());
            statement.setString(3, file.getContent());

            var result = statement.execute();

            id = GetLatestIdFromDb(con);

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

            var result = statement.execute();
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

            var result = statement.execute();
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
}
