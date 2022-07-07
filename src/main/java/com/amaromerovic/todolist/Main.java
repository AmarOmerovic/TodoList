package com.amaromerovic.todolist;

import datamodel.TodoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainWindow.fxml")));
        Scene scene = new Scene(root, 900, 692);
        stage.setTitle("ToDo List");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        try{
            TodoData.getInstance().storeTodoItems();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void init() throws Exception {
        try {
            TodoData.getInstance().loadTodoItems();
        }catch(IOException e){
                System.out.println(e.getMessage());
        }
    }


}