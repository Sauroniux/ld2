package com.example.ld1.fxControllers;

import com.example.ld1.data.AccountType;
import com.example.ld1.data.User;
import com.example.ld1.dbManagers.DbManager2;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegisterWindow implements Initializable
{
    @FXML
    private RadioButton isPerson;
    @FXML
    private RadioButton isCompany;

    @FXML
    private TextField username;
    @FXML
    private PasswordField password1;
    @FXML
    private PasswordField password2;

    @FXML
    private Label secondaryLabel1;
    @FXML
    private TextField secondaryText1;
    @FXML
    private Label secondaryLabel2;
    @FXML
    private TextField secondaryText2;

    public RegisterWindow()
    {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        var handler = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if(isPerson.isSelected())
                {
                    secondaryLabel1.setText("First name");
                    secondaryLabel2.setText("Second name");
                }
                else if (isCompany.isSelected())
                {
                    secondaryLabel1.setText("Company name");
                    secondaryLabel2.setText("Company email");
                }
            }
        };

        isPerson.setOnAction(handler);
        isCompany.setOnAction(handler);
    }

    public void OnClickCancel(ActionEvent actionEvent)
    {
        try
        {
            SceneManager.LoadScene(WindowResource.login);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void onRegisterClick(ActionEvent actionEvent) throws IOException
    {
        if(!isInputValid())
            return;

        AccountType accountType = isPerson.isSelected() ? AccountType.Person : AccountType.Company;
        User user = new User(username.getText(), password1.getText(), secondaryText1.getText(), secondaryText2.getText(), accountType);

        DbManager2.getInstance().CreateUser(user);

        SceneManager.LoadScene(WindowResource.login);
    }

    private boolean isInputValid()
    {

        if(username.getText().isEmpty())
        {
            SceneManager.ShowError("Username is empty!");
            return false;
        }

        if(!username.getText().matches("[A-Za-z0-9]+"))
        {
            SceneManager.ShowError("Username must be alphanumeric!");
            return false;
        }

        if(password1.getText().isEmpty())
        {
            SceneManager.ShowError("Password is empty");
            return false;
        }

        if(!password1.getText().matches("\\S+"))
        {
            SceneManager.ShowError("Password must not contain whitespace!");
            return false;
        }

        if(!Objects.equals(password1.getText(), password2.getText()))
        {
            SceneManager.ShowError("Passwords don't match!");
            return false;
        }

        if(secondaryText1.getText().isEmpty() || secondaryText2.getText().isEmpty())
        {
            SceneManager.ShowError("All fields must be filled out!");
            return false;
        }

        if(DbManager2.getInstance().doesUsernameExist(username.getText()))
        {
            SceneManager.ShowError("Username already exists!");
            return false;
        }

        return true;
    }
}
