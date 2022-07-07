package datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class TodoData {
    private static final TodoData instance = new TodoData();
    private static final String filename = "TodoListItems.txt";
    private static final String filenameForOldItems = "OutDatedItems.txt";

    private ObservableList<TodoItem> todoItems;
    private ObservableList<TodoItem> outDatedItems;
    private final DateTimeFormatter formatter;

    private TodoData(){
        formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    }

    public static TodoData getInstance(){
        return instance;
    }

    public ObservableList<TodoItem> getOutDatedItems() {
        return outDatedItems;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }
    public void addTodoItem(TodoItem item){
        todoItems.add(item);
    }
    public void addAsAnOldItem(TodoItem item){
        outDatedItems.add(item);
    }
    public void loadTodoItems() throws IOException{
        todoItems = FXCollections.observableArrayList();
        outDatedItems = FXCollections.observableArrayList();
        Path pathOne = Paths.get(filename);
        Path pathTwo = Paths.get(filenameForOldItems);
        BufferedReader br = Files.newBufferedReader(pathOne);
        BufferedReader brTwo = Files.newBufferedReader(pathTwo);

        String input;
        try{
            while ((input = br.readLine()) != null){
                String[] itemPieces = input.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);

                TodoItem todoData = new TodoItem(shortDescription, details, date);
                todoItems.add(todoData);
            }
        }finally {
            if (br != null) {
                br.close();
            }
        }


        String help;
        try{
            while ((help = brTwo.readLine()) != null){
                String[] itemPieces = help.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);

                TodoItem todoData = new TodoItem(shortDescription, details, date);
                outDatedItems.add(todoData);
            }
        }finally {
            if (brTwo != null) {
                brTwo.close();
            }
        }

    }
    public void storeTodoItems() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        Path pathTwo = Paths.get(filenameForOldItems);
        BufferedWriter bwTwo = Files.newBufferedWriter(pathTwo);
        try{
            Iterator<TodoItem> iterator = todoItems.iterator();
            while (iterator.hasNext()){
                TodoItem temp = iterator.next();
                bw.write(String.format("%s\t%s\t%s", temp.getShortDescription(), temp.getDetails(), temp.getDeadline().format(formatter)));
                bw.newLine();
            }

        }finally {
            if (bw != null){
                bw.close();
            }
        }

        try{
            Iterator<TodoItem> iterator = outDatedItems.iterator();
            while (iterator.hasNext()){
                TodoItem temp = iterator.next();
                bwTwo.write(String.format("%s\t%s\t%s", temp.getShortDescription(), temp.getDetails(), temp.getDeadline().format(formatter)));
                bwTwo.newLine();
            }

        }finally {
            if (bwTwo != null){
                bwTwo.close();
            }
        }
    }

    public void deleteTodoItem(TodoItem item){
        todoItems.remove(item);
    }


    public void deleteOldTodoItem(TodoItem item){
        outDatedItems.remove(item);
    }





}
