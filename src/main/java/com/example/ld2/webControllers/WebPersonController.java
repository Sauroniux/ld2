package com.example.ld2.webControllers;

        import com.example.ld1.data.Person;
        import org.springframework.http.HttpStatus;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import java.lang.reflect.InvocationTargetException;

@Controller //localhost:8080/application_context/company
public class WebPersonController extends BaseWebController<Person>
{
    public WebPersonController()
    {
        super(Person.class);
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
}