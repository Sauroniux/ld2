package com.example.ld1.fxControllers;

import com.example.ld1.data.Course;
import com.example.ld1.data.User;
import com.example.ld1.dbManagers.DbManager2;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateCourseWindowController
{
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;

    private HomeWindowController master;

    public static void CreateCourseDialog(HomeWindowController master) throws IOException
    {
        FXMLLoader loader = SceneManager.LoadPopUp(WindowResource.createCourse);
        var c = loader.getController();
        CreateCourseWindowController controller = (c instanceof CreateCourseWindowController ? (CreateCourseWindowController)c : null);

        if(controller == null)
            return;

        controller.master = master;
    }

    public void onCreateCourse(ActionEvent actionEvent)
    {
        User user = DbManager2.getInstance().getCurrentUser();

        if(user == null || !user.isCompany())
            return;

        Course course = new Course(titleField.getText(), descriptionField.getText(), 0);
        DbManager2.getInstance().CreateCourse(course);
        course.AppendModerator(user);
        DbManager2.getInstance().AddCourseOwner(user, course);
        course.createRootFolder();

        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
        master.initData();
    }

    public void onClickCancel(ActionEvent actionEvent)
    {
        Stage stage = (Stage)titleField.getScene().getWindow();
        stage.close();
    }
}
