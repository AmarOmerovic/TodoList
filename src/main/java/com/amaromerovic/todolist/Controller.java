package com.amaromerovic.todolist;

import datamodel.TodoData;
import datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    @FXML
    public ToggleButton filterToggleButton;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    public TextArea itemDetailsTextArea;
    @FXML
    public Label deadlineLabel;
    @FXML
    public BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ContextMenu listContextMenuTwo;
    @FXML
    private ListView<TodoItem> oldList;
    private FilteredList<TodoItem> filteredList;
    private Predicate<TodoItem> wantAllItems;
    private Predicate<TodoItem> wantUpcomingItems;




    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editMenuItem = new MenuItem("Edit");

        deleteMenuItem.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                deleteItem();
            }
        });

        editMenuItem.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                showEditItemDialog();
            }
        });

        listContextMenu.getItems().setAll(editMenuItem,deleteMenuItem);


        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
                if (newValue != null) {
                    TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });

        wantAllItems = new Predicate<>() {
            @Override
            public boolean test(TodoItem item) {
                return true;
            }
        };

        wantUpcomingItems = new Predicate<>() {
            @Override
            public boolean test(TodoItem item) {
                return item.getDeadline().isEqual(LocalDate.now()) || item.getDeadline().isEqual(LocalDate.now().plusDays(1));
            }
        };

        filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), wantAllItems);

        SortedList<TodoItem> sortedListActiveItems = new SortedList<>(filteredList, new Comparator<>() {
            @Override
            public int compare(TodoItem o1, TodoItem o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });




        todoListView.setItems(sortedListActiveItems);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText("• " + item.getShortDescription());
                            if (item.getDeadline().equals(LocalDate.now())) {
                                setFont(Font.font("Ubuntu Regular", 15));
                                setTextFill(Color.RED);
                            } else if (item.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setFont(Font.font("Ubuntu Regular", 15));
                                setTextFill(Color.CORAL);
                            } else if (item.getDeadline().isAfter(LocalDate.now())) {
                                setFont(Font.font("Ubuntu Regular", 15));
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );

                return cell;
            }
        });







        listContextMenuTwo = new ContextMenu();
        MenuItem item = new MenuItem("Delete");
        item.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                deleteItem();
            }
        });
        listContextMenuTwo.getItems().add(item);

        oldList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {

                if (newValue != null) {
                    TodoItem item = oldList.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });


        for (int i = 0; i < TodoData.getInstance().getTodoItems().size(); i++){
            if (TodoData.getInstance().getTodoItems().get(i).getDeadline().isBefore(LocalDate.now())){
                TodoData.getInstance().getOutDatedItems().add(TodoData.getInstance().getTodoItems().get(i));
                TodoData.getInstance().getTodoItems().remove(TodoData.getInstance().getTodoItems().remove(i));
            }
        }


        SortedList<TodoItem> sortedListOldItems = new SortedList<>(TodoData.getInstance().getOutDatedItems(), new Comparator<>() {
            @Override
            public int compare(TodoItem o1, TodoItem o2) {
                return o2.getDeadline().compareTo(o1.getDeadline());
            }
        });

        oldList.setItems(sortedListOldItems);
        oldList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        oldList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText("• " + item.getShortDescription());
                            setFont(Font.font("Ubuntu Regular", 15));
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenuTwo);
                            }
                        }
                );

                return cell;
            }
        });
    }


    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
            dialog.setTitle("New Item");
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            TodoItem newItem = controller.newItemProcessResult();
            if (newItem == null ){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Make sure you have entered all needed information's below!");
                Optional<ButtonType> error = alert.showAndWait();
                if ((error.isPresent()) && (error.get() == ButtonType.OK)) {
                    todoListView.getSelectionModel().selectFirst();
                }
            }else {
                todoListView.getSelectionModel().select(newItem);
            }
        }
    }


    public void handleClickListView() {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        oldList.getSelectionModel().clearSelection();
        try {
            itemDetailsTextArea.setText(item.getDetails());
            deadlineLabel.setText(df.format(item.getDeadline()));
        }catch (NullPointerException ignored){

        }
    }

    public void handleClickListViewTwo() {
        TodoItem item = oldList.getSelectionModel().getSelectedItem();
        todoListView.getSelectionModel().clearSelection();
        try {
            itemDetailsTextArea.setText(item.getDetails());
            deadlineLabel.setText(df.format(item.getDeadline()));
        }catch (NullPointerException ignored){

        }


    }

    public void deleteItem() {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        if (item == null){
            item = oldList.getSelectionModel().getSelectedItem();
        }
        try{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete item");
            alert.setHeaderText("Delete item: " + item.getShortDescription());
            alert.setContentText("Are you sure?\nPress OK to confirm, or cancel to Back out.");
            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                if (TodoData.getInstance().getTodoItems().contains(item)){
                    TodoData.getInstance().deleteTodoItem(item);
                    if (TodoData.getInstance().getTodoItems().size() == 0 && TodoData.getInstance().getOutDatedItems().size() == 0){
                        itemDetailsTextArea.clear();
                        deadlineLabel.setText("");
                    } else if (todoListView.getItems().size() == 0){
                        oldList.getSelectionModel().selectFirst();
                    }else {
                        todoListView.getSelectionModel().selectFirst();
                    }
                }else {
                    TodoData.getInstance().deleteOldTodoItem(item);
                    if (TodoData.getInstance().getTodoItems().size() == 0 && TodoData.getInstance().getOutDatedItems().size() == 0){
                        itemDetailsTextArea.clear();
                        deadlineLabel.setText("");
                    }
                    todoListView.getSelectionModel().selectFirst();
                }
            }
        }catch (NullPointerException ignore){

        }

    }

    public void deleteItemWithKey(KeyEvent keyEvent) {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        if (item == null){
            item = oldList.getSelectionModel().getSelectedItem();
        }
        if (item != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete item");
                alert.setHeaderText("Delete item: " + item.getShortDescription());
                alert.setContentText("Are you sure?\nPress OK to confirm, or cancel to Back out.");
                Optional<ButtonType> result = alert.showAndWait();
                if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                    if (TodoData.getInstance().getTodoItems().contains(item)){
                        TodoData.getInstance().deleteTodoItem(item);

                        if (TodoData.getInstance().getTodoItems().size() == 0 && TodoData.getInstance().getOutDatedItems().size() == 0){
                            itemDetailsTextArea.clear();
                            deadlineLabel.setText("");
                        } else if (todoListView.getItems().size() == 0){
                            oldList.getSelectionModel().selectFirst();
                        }else {
                            todoListView.getSelectionModel().selectFirst();
                        }

                    }else {
                        TodoData.getInstance().deleteOldTodoItem(item);
                        if (TodoData.getInstance().getTodoItems().size() == 0 && TodoData.getInstance().getOutDatedItems().size() == 0){
                            itemDetailsTextArea.clear();
                            deadlineLabel.setText("");
                        }
                        todoListView.getSelectionModel().selectFirst();
                    }
                }
            }
        }
    }

    public void showEditItemDialog() {
        TodoItem item = oldList.getSelectionModel().getSelectedItem();
        if (item != null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You can't edit outdated items!");
            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                return;
            }
        }

        if (TodoData.getInstance().getOutDatedItems().size() == 0 && TodoData.getInstance().getTodoItems().size() == 0){
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
            dialog.setTitle("Edit Item");
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            item = todoListView.getSelectionModel().getSelectedItem();
            if (item != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Edit item");
                alert.setHeaderText("Edit item: " + item.getShortDescription());
                alert.setContentText("Are you sure?\nPress OK to confirm, or cancel to Back out.");
                Optional<ButtonType> confirmation = alert.showAndWait();
                if ((confirmation.isPresent()) && (confirmation.get() == (ButtonType.OK))) {
                    if (controller.editItemProcessResult(item)) {
                        if (item.getDeadline().isBefore(LocalDate.now())){
                            TodoData.getInstance().deleteTodoItem(item);
                            TodoData.getInstance().addAsAnOldItem(item);
                        }
                        todoListView.getSelectionModel().select(item);
                        initialize();
                    }else {
                        Alert alertTwo = new Alert(Alert.AlertType.ERROR);
                        alertTwo.setTitle("Error");
                        alertTwo.setHeaderText(null);
                        alertTwo.setContentText("Make sure you have entered all needed information's below!");
                        Optional<ButtonType> error = alertTwo.showAndWait();
                        if ((error.isPresent()) && (error.get() == ButtonType.OK)) {
                            todoListView.getSelectionModel().selectFirst();
                        }
                    }
                }
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Make sure you have entered all needed information's below!");
                Optional<ButtonType> error = alert.showAndWait();
                if ((error.isPresent()) && (error.get() == ButtonType.OK)) {
                    todoListView.getSelectionModel().selectFirst();
                }
            }
        }
    }

    public void exit(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to quit?\nPress OK to confirm, or cancel to Back out.");
        Optional<ButtonType> optionalButton = alert.showAndWait();
        if ((optionalButton.isPresent()) && (optionalButton.get() == ButtonType.OK)){

            Platform.exit();
        }
    }


    public void handleFilterButton(){
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()){
            filteredList.setPredicate(wantUpcomingItems);
            if (filteredList.isEmpty()){
                itemDetailsTextArea.clear();
                deadlineLabel.setText("");
            } else if (filteredList.contains(selectedItem)){
                todoListView.getSelectionModel().select(selectedItem);
            }else {
                todoListView.getSelectionModel().selectFirst();
            }
        }else {
            filteredList.setPredicate(wantAllItems);
            todoListView.getSelectionModel().select(selectedItem);
        }

    }
}
