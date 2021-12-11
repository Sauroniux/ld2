package com.example.ld2.webControllers;

import com.example.ld1.data.File;
import com.example.ld1.dbManagers.FileDbManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
public class WebFileController extends BaseWebController<File> {
    public WebFileController()
    {
        super(File.class);
    }

    @RequestMapping(value = "/file/allFiles", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllFiles()
    {
        return getAll();
    }

    @RequestMapping(value = "/file/updateFile/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateFile(@RequestBody String request, @PathVariable(name = "id") int id) throws IllegalAccessException, InvocationTargetException {
        return update(request, id);
    }

    @RequestMapping(value = "/file/addFile", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addNewFile(@RequestBody String request) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return create(request);
    }

    @RequestMapping(value = "/file/deleteFile/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteFile(@PathVariable(name = "id") int id) {
        return delete(id);
    }

    @Override
    protected List<File> getAllFromDb()
    {
        return FileDbManager.getInstance().GetAllFiles();
    }

    @Override
    protected File getByIdFromDb(int id)
    {
        return FileDbManager.getInstance().GetFileById(id);
    }

    @Override
    protected void updateInDb(File object)
    {
        FileDbManager.getInstance().UpdateFile(object);
    }

    @Override
    protected void deleteFromDb(int id)
    {
        FileDbManager.getInstance().DeleteFile(id);
    }
}
