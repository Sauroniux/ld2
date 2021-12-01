package com.example.ld2.webControllers;

import com.example.ld1.data.Company;
import com.example.ld1.data.Company;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;

@Controller
public class WebCompanyController extends BaseWebController<Company> {
    public WebCompanyController()
    {
        super(Company.class);
    }

    @RequestMapping(value = "/company/allCompanies", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllCompanies()
    {
        return getAll();
    }

    @RequestMapping(value = "/company/updateCompany/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateCompany(@RequestBody String request, @PathVariable(name = "id") int id) throws IllegalAccessException, InvocationTargetException {
        return update(request, id);
    }

    @RequestMapping(value = "/company/addCompany", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addNewCompany(@RequestBody String request) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return create(request);
    }

    @RequestMapping(value = "/company/deleteCompany/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteCompany(@PathVariable(name = "id") int id) {
        return delete(id);
    }
}
