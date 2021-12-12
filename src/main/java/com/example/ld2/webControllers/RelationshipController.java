package com.example.ld2.webControllers;

import com.example.ld1.data.Course;
import com.example.ld1.data.User;
import com.example.ld1.dbManagers.RelationshipDbManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class RelationshipController
{
    private Gson getGson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @RequestMapping(value = "/rel/addOwner/{userId}/{courseId}}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String AddNewOwnerPair(@PathVariable(name = "userId") int userId, @PathVariable(name = "courseId") int courseId)
    {
        return AddPair(userId, courseId, (a, b) -> RelationshipDbManager.getInstance().AddCourseOwner(a, b));
    }

    @RequestMapping(value = "/rel/addModerator/{userId}/{courseId}}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String AddNewModeratorPair(@PathVariable(name = "userId") int userId, @PathVariable(name = "courseId") int courseId)
    {
        return AddPair(userId, courseId, (a, b) -> RelationshipDbManager.getInstance().AddCourseModerator(a, b));
    }

    @RequestMapping(value = "/rel/addViewer/{userId}/{courseId}}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String AddNewViewerPair(@PathVariable(name = "userId") int userId, @PathVariable(name = "courseId") int courseId)
    {
        return AddPair(userId, courseId, (a, b) -> RelationshipDbManager.getInstance().AddCourseViewer(a, b));
    }

    @RequestMapping(value = "/rel/removeModerator/{userId}/{courseId}}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String RemoveModeratorPair(@PathVariable(name = "userId") int userId, @PathVariable(name = "courseId") int courseId)
    {
        return AddPair(userId, courseId, (a, b) -> RelationshipDbManager.getInstance().RemoveCourseModerator(a, b));
    }

    @RequestMapping(value = "/rel/removeViewer/{userId}/{courseId}}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String RemoveViewerPair(@PathVariable(name = "userId") int userId, @PathVariable(name = "courseId") int courseId)
    {
        return AddPair(userId, courseId, (a, b) -> RelationshipDbManager.getInstance().RemoveCourseViewer(a, b));
    }

    @RequestMapping(value = "/rel/getModerated/{userId}/}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String GetModerated(@PathVariable(name = "userId") int userId)
    {
        return GetUserCourses(userId, (a) -> RelationshipDbManager.getInstance().GetModeratedCoursesObjects(a));
    }

    @RequestMapping(value = "/rel/getOwned/{userId}/}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String GetOwned(@PathVariable(name = "userId") int userId)
    {
        return GetUserCourses(userId, (a) -> RelationshipDbManager.getInstance().GetOwnedCourses(a));
    }

    @RequestMapping(value = "/rel/getViewed/{userId}/}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String GetViewed(@PathVariable(name = "userId") int userId)
    {
        return GetUserCourses(userId, (a) -> RelationshipDbManager.getInstance().GetViewedCoursesObjects(a));
    }

    @RequestMapping(value = "/rel/getModerators/{courseId}/}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String GetModerators(@PathVariable(name = "courseId") int courseId)
    {
        return GetCourseUsers(courseId, (a) -> RelationshipDbManager.getInstance().GetCourseModeratorsObjects(a));
    }

    @RequestMapping(value = "/rel/getViewers/{courseId}/}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String GetViewers(@PathVariable(name = "courseId") int courseId)
    {
        return GetCourseUsers(courseId, (a) -> RelationshipDbManager.getInstance().GetCourseViewersObjects(a));
    }

    private interface TwoIdOperation {
        public void op(User user, Course course);
    }

    private String AddPair(int userId, int courseId, TwoIdOperation operation)
    {
        var tempUser = new User();
        tempUser.setId(userId);

        var tempCourse = new Course();
        tempCourse.setId(courseId);

        operation.op(tempUser, tempCourse);

        return "True";
    }

    private interface GetUserCourses {
        public List<Course> op(User user);
    }

    private String GetUserCourses(int userId, GetUserCourses operation)
    {
        var tempUser = new User();
        tempUser.setId(userId);

        var resultList = operation.op(tempUser);

        Gson gson = getGson();
        return gson.toJson(resultList);
    }

    private interface GetCourseUsers {
        public List<User> op(Course course);
    }

    private String GetCourseUsers(int courseId, GetCourseUsers operation)
    {
        var tempCourse = new Course();
        tempCourse.setId(courseId);

        var resultList = operation.op(tempCourse);

        Gson gson = getGson();
        return gson.toJson(resultList);
    }
}
