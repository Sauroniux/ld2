package com.example.ld2.webControllers;

import com.example.ld1.data.Course;
import com.example.ld1.dbManagers.DbManager2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
public class WebCourseController extends BaseWebController<Course>
{
    public WebCourseController()
    {
        super(Course.class);
    }

    @RequestMapping(value = "/course/allCourses", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllCourses()
    {
        return getAll();
    }

    @RequestMapping(value = "/course/updateCourse/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateCourse(@RequestBody String request, @PathVariable(name = "id") int id) throws IllegalAccessException, InvocationTargetException {
        return update(request, id);
    }

    @RequestMapping(value = "/course/addCourse", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addNewCourse(@RequestBody String request) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return create(request);
    }

    @RequestMapping(value = "/course/deleteCourse/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteCourse(@PathVariable(name = "id") int id) {
        return delete(id);
    }

    @Override
    protected List<Course> getAllFromDb()
    {
        return DbManager2.getInstance().GetAllCourses();
    }

    @Override
    protected Course getByIdFromDb(int id)
    {
        return DbManager2.getInstance().GetCourseById(id);
    }

    @Override
    protected void updateInDb(Course object)
    {
        DbManager2.getInstance().UpdateCourse(object);
    }

    @Override
    protected void deleteFromDb(int id)
    {
        DbManager2.getInstance().DeleteCourse(id);
    }
}
