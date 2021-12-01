package com.example.ld1.fxControllers;

import com.example.ld1.data.BaseUser;
import com.example.ld1.dbManagers.DbManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

public class LoginWindow implements IController
{
    @FXML
    private TextField m_Username;
    @FXML
    private PasswordField m_PasswordField;

    private void ShowError()
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Incorrect credentials");
        alert.setHeaderText(null);
        alert.setContentText(m_Username.getText() + " " + m_PasswordField.getText());
        alert.showAndWait();
    }

    private void ShowSuccess()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Successful login!");
        alert.setHeaderText(null);
        alert.setContentText(m_Username.getText() + " " + m_PasswordField.getText());
        alert.showAndWait();
    }

    public void OnLoginClick(ActionEvent actionEvent) throws IOException
    {
        BaseUser user = DbManager.getInstance().CheckLogin(m_Username.getText(), m_PasswordField.getText());

        if(user == null)
            user = DbManager.getInstance().CheckLogin(m_Username.getText(), m_PasswordField.getText());

        boolean isValidLogin = user != null;

        if(!isValidLogin)
        {
            ShowError();
        }
        else
        {
            DbManager.getInstance().setCurrentUser(user);
            SceneManager.LoadScene(WindowResource.home);
        }
    }

    public void OnRegisterClick(ActionEvent actionEvent)
    {
        try
        {
            SceneManager.LoadScene(WindowResource.register);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void OnLoad(Scene scene)
    {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                try
                {
                    OnLoginClick(null);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
    }
}
