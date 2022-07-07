package com.amaromerovic.todolist;

import datamodel.TodoData;
import datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsField;
    @FXML
    private DatePicker datePickerField;

    public TodoItem newItemProcessResult(){
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsField.getText().trim();
        LocalDate date = datePickerField.getValue();

        if (shortDescription.isEmpty() || details.isEmpty() || date == null){
            return null;
        }else {
            TodoItem newItem = new TodoItem(shortDescription, details, date);
            if (date.isBefore(LocalDate.now())) {
                TodoData.getInstance().addAsAnOldItem(newItem);
            } else {
                TodoData.getInstance().addTodoItem(newItem);
            }
            return newItem;
        }
    }

    public boolean editItemProcessResult(TodoItem item){
        if (item != null){
            String shortDescription = shortDescriptionField.getText().trim();
            String details = detailsField.getText().trim();
            LocalDate date = datePickerField.getValue();

            if (shortDescription.isEmpty() || details.isEmpty() || date == null){
                return false;
            }else {
                item.setShortDescription(shortDescription);
                item.setDetails(details);
                item.setDeadline(date);
            }
        }
        return true;
    }
}
