package com.example.ld2.webControllers;

import com.example.ld1.dbManagers.UserDbManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

@Controller
public class WebLoginController
{
    class LoginInfo
    {
        public String username;
        public String password;
    }

    @RequestMapping(value = "/login/checkLogin", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String checkLogin(@RequestBody String request)
    {
        Gson gson = getGson();
        LoginInfo loginInfo = gson.fromJson(request, LoginInfo.class);

        var result = UserDbManager.getInstance().LoginUser(loginInfo.username, loginInfo.password);

        if(result == null || result.getId() <= 0)
            return "False";

        return "True";
    }

    private Gson getGson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }
}
