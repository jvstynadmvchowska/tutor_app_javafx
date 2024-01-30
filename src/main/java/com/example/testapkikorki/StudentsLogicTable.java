package com.example.testapkikorki;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.*;
import java.text.DecimalFormat;

public class StudentsLogicTable {

    @FXML
    private TextField filterTextField;

    @FXML
    private TableView<Generic> tableView;

    @FXML
    private TableColumn<Generic, String> nameColumn, hoursWeeklyColumn;

    @FXML
    private TableColumn<Generic, Double> rateColumn;


    final String DB_URL = "jdbc:mysql://localhost:3306/myapp1", USERNAME = "root", PASSWORD = "root";
    public BorderPane studentsInTableBorderPane;

    // coordinates used for moving application across the screen
    private double x = 0, y = 0;



    @FXML
    public void initialize(){
        try{
            nameColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("name"));
            rateColumn.setCellValueFactory(new PropertyValueFactory<Generic, Double>("rate"));
            hoursWeeklyColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Generic, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Generic, String> param) {
                    Generic student = param.getValue();

                    if (student.getHoursWeekly() == null || student.getHoursWeekly() == 0.0) {
                        return new SimpleStringProperty("not specified");
                    } else {
                        double hours = student.getHoursWeekly();
                        return new SimpleStringProperty(String.valueOf(hours));
                    }
                }
            });
            centerColumn(nameColumn);
            centerColumn(hoursWeeklyColumn);
            rateColumn.setCellFactory(tc -> {
                TextFieldTableCell<Generic, Double> cell = new TextFieldTableCell<>();
                cell.setAlignment(javafx.geometry.Pos.CENTER);
                return cell;
            });
            tableView.setItems(getStudentsFromDatabase());
            FilteredList<Generic> filtered = new FilteredList<>(getStudentsFromDatabase(), b -> true);
            filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtered.setPredicate( object -> {
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if(object.getName().toLowerCase().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if(String.valueOf(object.getRate()).indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if(String.valueOf(object.getHoursWeekly()).indexOf(lowerCaseFilter) != -1){
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
        Stage stage = (Stage) studentsInTableBorderPane.getScene().getWindow();
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
    private ObservableList<Generic> getStudentsFromDatabase(){
        String sql = "SELECT * FROM students;";
        ObservableList<Generic> studentsList = FXCollections.observableArrayList();
        String name, rate;
        Double hoursWeekly;

        try{
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultStatement = preparedStatement.executeQuery();

            while(resultStatement.next()){
                DecimalFormat decimalFormat = new DecimalFormat("0.00 zl/h");
                name = resultStatement.getString("name");
                rate = resultStatement.getString("rate");
                rate = String.valueOf(decimalFormat.format(Double.parseDouble(rate)));
                if(resultStatement.getString("hoursWeekly") == null || Double.parseDouble(resultStatement.getString("hoursWeekly")) == 0.0){
                    studentsList.add(new Generic(name,rate, 0.0));
                }
                else{
                    hoursWeekly = Double.parseDouble(resultStatement.getString("hoursWeekly"));
                    studentsList.add(new Generic(name,rate, hoursWeekly));
                }
            }


        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return studentsList;
    }

}
