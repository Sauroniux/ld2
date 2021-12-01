package com.example.ld2.webControllers;

import com.example.ld1.data.BaseUser;
import com.example.ld1.data.Company;
import com.example.ld1.data.Person;
import com.example.ld1.dbManagers.DbManager;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;

@Controller
public class WebLoginController extends BaseWebController<BaseUser>
{
    class LoginInfo
    {
        public String username;
        public String password;
    }

    protected WebLoginController() {
        super(BaseUser.class);
    }

    @RequestMapping(value = "/login/checkLogin", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String checkLogin(@RequestBody String request)
    {
        Gson gson = getGson();
        LoginInfo loginInfo = gson.fromJson(request, LoginInfo.class);

        var allPeople = DbManager.getInstance().<Person>GetAll(Person.class);

        for(var person : allPeople)
        {
            if(person.getUsername().equals(loginInfo.username) && person.getPassword().equals(loginInfo.password))
                return "True";
        }

        var allCompanies = DbManager.getInstance().<Company>GetAll(Company.class);

        for(var company : allCompanies)
        {
            if(company.getUsername().equals(loginInfo.username) && company.getPassword().equals(loginInfo.password))
                return "True";
        }

        return "False";
    }
}
