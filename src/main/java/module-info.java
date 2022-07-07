module com.amaromerovic.todolist {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.amaromerovic.todolist to javafx.fxml;
    exports com.amaromerovic.todolist;
}