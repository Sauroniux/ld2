package com.example.ld2.webControllers;

import com.example.ld1.data.Folder;
import com.example.ld1.dbManagers.FolderDbManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
public class WebFolderController extends BaseWebController<Folder> {
    public WebFolderController()
    {
        super(Folder.class);
    }

    @RequestMapping(value = "/folder/allFolders", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllFolders()
    {
        return getAll();
    }

    @RequestMapping(value = "/folder/updateFolder/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateFolder(@RequestBody String request, @PathVariable(name = "id") int id) throws IllegalAccessException, InvocationTargetException {
        return update(request, id);
    }

    @RequestMapping(value = "/folder/addFolder", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addNewFolder(@RequestBody String request) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return create(request);
    }

    @RequestMapping(value = "/folder/deleteFolder/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteFolder(@PathVariable(name = "id") int id) {
        return delete(id);
    }

    @Override
    protected List<Folder> getAllFromDb()
    {
        return FolderDbManager.getInstance().GetAllFolders();
    }

    @Override
    protected Folder getByIdFromDb(int id)
    {
        return FolderDbManager.getInstance().GetFolderById(id);
    }

    @Override
    protected void updateInDb(Folder object)
    {
        FolderDbManager.getInstance().UpdateFolder(object);
    }

    @Override
    protected void deleteFromDb(int id)
    {
        FolderDbManager.getInstance().DeleteFolder(id);
    }
}
