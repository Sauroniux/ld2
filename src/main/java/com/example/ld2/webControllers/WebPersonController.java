package com.example.ld2.webControllers;

        import com.example.ld1.data.User;
        import com.example.ld1.dbManagers.UserDbManager;
        import org.springframework.http.HttpStatus;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import java.lang.reflect.InvocationTargetException;
        import java.util.List;

@Controller //localhost:8080/application_context/company
public class WebPersonController extends BaseWebController<User>
{
    public WebPersonController()
    {
        super(User.class);
    }

    @RequestMapping(value = "/person/allPeople", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllUsers()
    {
        return getAll();
    }

    @RequestMapping(value = "/person/updatePerson/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateUser(@RequestBody String request, @PathVariable(name = "id") int id) throws IllegalAccessException, InvocationTargetException {
        return update(request, id);
    }

    @RequestMapping(value = "/person/addPerson", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addNewPerson(@RequestBody String request) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return create(request);
    }

    @RequestMapping(value = "/person/deletePerson/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deletePerson(@PathVariable(name = "id") int id) {
        return delete(id);
    }

    @Override
    protected List<User> getAllFromDb()
    {
        return UserDbManager.getInstance().GetAllUsers();
    }

    @Override
    protected User getByIdFromDb(int id)
    {
        return UserDbManager.getInstance().GetUserById(id);
    }

    @Override
    protected void updateInDb(User object)
    {
        UserDbManager.getInstance().UpdateUser(object);
    }

    @Override
    protected void deleteFromDb(int id)
    {
        UserDbManager.getInstance().DeleteUser(id);
    }
}