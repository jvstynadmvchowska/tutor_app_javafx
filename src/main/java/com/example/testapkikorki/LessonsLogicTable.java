package com.example.testapkikorki;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LessonsLogicTable {

    @FXML
    private TextField filterTextField;

    @FXML
    private DatePicker fromDateDatePicker, toDateDatePicker;

    @FXML
    private TableView<Generic> tableView;

    @FXML
    private TableColumn<Generic, String> nameColumn, dateColumn, hoursColumn, timeColumn, typeColumn;


    //all essential information to connect to database
    final String DB_URL = "jdbc:mysql://localhost:3306/myapp1", USERNAME = "root", PASSWORD = "root";
    public BorderPane lessonsInTableBorderPane;

    // coordinates used for moving application across the screen
    private double x = 0, y = 0;


    //method initializing all essential components at the beginning of running lessons page
    @FXML
    public void initialize(){
        try{
            nameColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("name"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("date"));
            hoursColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("hours"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("time"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("typeOfLesson"));
            centerColumn(nameColumn);
            centerColumn(dateColumn);
            centerColumn(hoursColumn);
            centerColumn(timeColumn);
            centerColumn(typeColumn);

            tableView.setItems(getLessonsForTable());
            FilteredList<Generic> filtered = new FilteredList<>(getLessonsForTable(), b -> true);
            filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtered.setPredicate( object -> {
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if(String.valueOf(object.getTime()).indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if(object.getName().toLowerCase().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if(object.getTypeOfLesson().toLowerCase().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if(object.getDate().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if(object.getHours().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else{
                        return false;
                    }

                });

            });

            SortedList<Generic> sorted = new SortedList<>(filtered);
            sorted.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sorted);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
        methods movingWhenDragged and movingWhenPressed are used for moving the application across the screen
        when user grabs and moves it around with mouse click
     */
    @FXML
    public void movingWhenDragged(MouseEvent event){
        Stage stage = (Stage) lessonsInTableBorderPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void movingWhenPressed(MouseEvent event){
        x = event.getSceneX();
        y = event.getSceneY();
    }


    public void centerColumn(TableColumn<Generic, String> nameOfColumn){
        nameOfColumn.setCellFactory(tc -> {
            TextFieldTableCell<Generic, String> cell = new TextFieldTableCell<>();
            cell.setAlignment(javafx.geometry.Pos.CENTER);
            return cell;
        });
    }

    //method for filtering records in table by dates
    public void checkByDates(ActionEvent event) throws IOException {
        LocalDate fromDate = fromDateDatePicker.getValue();
        LocalDate toDate = toDateDatePicker.getValue();
        String answer = "";

        if(toDate != null || fromDate != null){
            if(fromDate == null){
                fromDate = LocalDate.of(2023,12,1);
            }
            if(toDate == null){
                toDate = LocalDate.now();
            }
            if(ChronoUnit.DAYS.between(fromDate, toDate) < 0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setContentText("Your data \"from date\" is later than your \"to date\"");
                alert.showAndWait();
            }
            if(ChronoUnit.DAYS.between(fromDate, toDate) == 0){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                ButtonType option1 = new ButtonType("OK");
                ButtonType cancel = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
                DialogPane dialogPane = new DialogPane();
                dialogPane.getButtonTypes().addAll(option1,  cancel);
                alert.setDialogPane(dialogPane);
                alert.setTitle("ARE YOU SURE?");
                alert.setContentText("Your data \"from date\" is the same as your \"to date\"");
                alert.showAndWait();
                answer = alert.getResult().getText();
            }
            if(ChronoUnit.DAYS.between(fromDate, toDate) > 0){
                answer = "OK";
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("NO FILTER");
            alert.setContentText("Your savings are filtered the same way as they were before.");
            alert.showAndWait();
            reset(event);
        }

        if(fromDate != null && toDate != null && answer.equals("OK")) {
            tableView.setItems(getLessonsForTableByDates(fromDate.toString(), toDate.toString()));
            FilteredList<Generic> filtered = new FilteredList<>(getLessonsForTableByDates(fromDate.toString(), toDate.toString()), b -> true);
            filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtered.setPredicate( object -> {
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();


                    if(object.getQuantity().toLowerCase().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if(object.getDate().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else{
                        return false;
                    }

                });

            });

            SortedList<Generic> sorted = new SortedList<>(filtered);
            sorted.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sorted);
        }

    }

    //method for resetting filtering by date in table view
    public void reset(Event event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/lessonsInTable.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        boolean fs = stage.isFullScreen();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setFullScreen(fs);
        stage.show();
    }
    private ObservableList<Generic> getLessonsForTableByDates(String fromDate, String toDate){
        String sqlQuery = "SELECT * FROM lessons WHERE data BETWEEN ? AND ?;";

        ObservableList<Generic> lessonsList = FXCollections.observableArrayList();
        String name, date, hours, time, typeOfLesson;

        try{
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, fromDate);
            preparedStatement.setString(2, toDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                name = resultSet.getString("name");
                date = resultSet.getString("date");
                hours = resultSet.getString("quantity");
                time = resultSet.getString("time");
                typeOfLesson = resultSet.getString("typeOfLesson");
                lessonsList.add(new Generic(name, date, hours, time, typeOfLesson));
            }


        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return lessonsList;

    }
    private ObservableList<Generic> getLessonsForTable(){
        String sqlQuery = "SELECT * FROM lessons;";

        ObservableList<Generic> lessons = FXCollections.observableArrayList();
        String name, date, hours, time, typeOfLesson;

        try{
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                name = resultSet.getString("name");
                date = resultSet.getString("date");
                hours = resultSet.getString("hours");
                time = resultSet.getString("time");
                typeOfLesson = resultSet.getString("typeOfLesson");
                lessons.add(new Generic(name,date,hours, time, typeOfLesson));
            }


        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return lessons;
    }

}
