package com.example.ld1;

import com.example.ld1.fxControllers.SceneManager;
import com.example.ld1.fxControllers.WindowResource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application
{
    @Override
    public void start(Stage stage) throws IOException, InterruptedException
    {
        StartDocker();

        SceneManager.SetStage(stage);
        SceneManager.LoadScene(WindowResource.login);
    }

    private static void StartDocker() throws IOException, InterruptedException
    {
        String cmd0 = "docker start my-own-mysql";
        String cmd1 = "docker start my-own-phpmyadmin";
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(cmd0);
        pr.waitFor();
        pr = run.exec(cmd1);
        pr.waitFor();
    }

    public static void main(String[] args)
    {
        launch();
    }
}