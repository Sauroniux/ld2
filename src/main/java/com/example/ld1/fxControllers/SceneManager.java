package com.example.ld1.fxControllers;

import com.example.ld1.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager
{
    private static Stage s_Stage;

    public static void SetStage(Stage stage)
    {
        s_Stage = stage;
    }

    public static void LoadScene(WindowResource resource) throws IOException
    {
        LoadSceneWithStage(resource, s_Stage);
    }

    public static FXMLLoader LoadPopUp(WindowResource resource) throws IOException
    {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(s_Stage);

        return LoadSceneWithStage(resource, dialog);
    }

    public static FXMLLoader LoadSceneWithStage(WindowResource resource, Stage stage) throws IOException
    {
        String location = resource.toString();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(location));
        Scene scene = new Scene(fxmlLoader.load());

        var controller = fxmlLoader.getController();
        IController iController = (controller instanceof IController ? (IController)controller : null);

        if(iController != null)
            iController.OnLoad(scene);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        return fxmlLoader;
    }

    public static void ShowError(String errorMessage)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(errorMessage);
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
