package com.example.testapkikorki;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.regex.Pattern;

public class StudentsLogic {

    @FXML
    private TextField addNameTextField, addHoursWeeklyTextField, editHoursWeeklyTextField
            , editNameTextField, addRateTextField, editRateTextField;

    @FXML
    private ChoiceBox<String> editStudentChoiceBox, removeStudentChoiceBox;


    public BorderPane studentsBorderPane;

    // coordinates used for moving application across the screen
    private double x = 0, y = 0;

    //all essential information to connect to database
    final String DB_URL = "jdbc:mysql://localhost:3306/myapp1", USERNAME = "root", PASSWORD = "root";
    private Stage stage = new Stage();
    private FXMLLoader fxmlLoader;
    private Scene scene;
    ObservableList<String> studentsNamesList = getStudents();
    public Student student;



    //method initializing all essential components at the beginning of running lessons page
    @FXML
    private void initialize() {
        try {
            removeStudentChoiceBox.setItems(studentsNamesList);
            editStudentChoiceBox.setItems(studentsNamesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void minimizeWindow(MouseEvent e) {
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setIconified(true);
    }
    @FXML
    public void maximiseWindow(MouseEvent e) {
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setFullScreenExitHint("");
        if (s.isFullScreen()) {
            s.setFullScreen(false);
        } else {
            s.setFullScreen(true);
        }

    }

    @FXML
    public void closeWindow(MouseEvent e) {
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.close();
    }

    /*
        methods movingWhenDragged and movingWhenPressed are used for moving the application across the screen
        when user grabs and moves it around with mouse click
     */
    @FXML
    public void movingWhenDragged(MouseEvent event){
        Stage stage = (Stage) studentsBorderPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void movingWhenPressed(MouseEvent event){
        x = event.getSceneX();
        y = event.getSceneY();
    }


    public void showStudentsList(Event event) throws IOException {
        Stage stage2 = new Stage();
        FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("fxml/studentsInTable.fxml"));
        Scene scene2 = new Scene(fxmlLoader2.load());
        stage2.initOwner((Stage) ((Node) event.getSource()).getScene().getWindow());
        stage2.setScene(scene2);
        InputStream inputStream = HelloApplication.class.getResourceAsStream("/png/logo.png");
        if (inputStream != null) {
            stage2.getIcons().add(new Image(inputStream));
        }
        stage2.setTitle("List of your students!! :)");
        stage2.show();
    }

    // method for providing list of objects to appear in choice box
    public ObservableList<String> getStudents() {
        ObservableList<String> studentsList = FXCollections.observableArrayList();
        String sqlQuery = "SELECT * FROM students";

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String value = "";
                value += resultSet.getString("name") + " ";
                Double rate = resultSet.getDouble("rate");
                value += rate + " ";
                if (resultSet.getDouble("hoursWeekly") != 0.0) {
                    value += resultSet.getDouble("hoursWeekly") + " ";
                }
                studentsList.add(value.trim());
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return studentsList;

    }
    public void addStudent(ActionEvent event) throws IOException {
        Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{2}");
        Pattern pattern2 = Pattern.compile("\\d{1,3}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,3}");
        Pattern patternComma = Pattern.compile("\\d{1,3},\\d{2}");
        Pattern patternComma2 = Pattern.compile("\\d{1,3},\\d{1}");

        if(addNameTextField.getText().isEmpty() && addRateTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
            alert.setContentText("Please, enter all required data do add a student succesfully.");
            showAlertOnFS(alert, event);
        }
        else if(addNameTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
            alert.setContentText("Please, enter name to add a student succesfully.");
            showAlertOnFS(alert, event);
        }
        else if(addRateTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
            alert.setContentText("Please, enter rate to add a student succesfully.");
            showAlertOnFS(alert, event);
        }
        else{
            String name = addNameTextField.getText();
            Double rate;
            if ((pattern.matcher(addRateTextField.getText()).matches()
                    || pattern2.matcher(addRateTextField.getText()).matches()
                    || pattern3.matcher(addRateTextField.getText()).matches())) {
                if(Double.parseDouble(addRateTextField.getText()) <= 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                    alert.setContentText("Rate shouldn't be equal or less than zero.");
                    showAlertOnFS(alert, event);
                }
                else{
                    rate = Double.parseDouble(addRateTextField.getText());
                    if (addHoursWeeklyTextField.getText().isEmpty()) {
                        student = addStudentToDatabase(name, rate);
                        if (student == null) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                            alert.setContentText("Please, enter all required data do add a student succesfully.");
                            showAlertOnFS(alert, event);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("SUCCESS");
                            alert.setContentText("Student is added correctly. :)");
                            showAlertOnFS(alert, event);
                            switchToStudents(event);
                        }
                    } else {
                        if(patternComma.matcher(addHoursWeeklyTextField.getText()).matches() || patternComma2.matcher(addHoursWeeklyTextField.getText()).matches()){
                            Double hoursWeekly = Double.parseDouble(addHoursWeeklyTextField.getText().replace(",","."));
                            student = addStudentToDatabase(name, rate, hoursWeekly);
                            if (student == null) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                                alert.setContentText("Please, enter all required data do add a student succesfully.");
                                showAlertOnFS(alert, event);
                            } else {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("SUCCESS");
                                alert.setContentText("Student is added correctly. :)");
                                showAlertOnFS(alert, event);
                                switchToStudents(event);
                            }
                        }
                        else{
                            Double hoursWeekly = Double.parseDouble(addHoursWeeklyTextField.getText());
                            student = addStudentToDatabase(name, rate, hoursWeekly);
                            if (student == null) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                                alert.setContentText("Please, enter all required data do add a student succesfully.");
                                showAlertOnFS(alert, event);
                            } else {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("SUCCESS");
                                alert.setContentText("Student is added correctly. :)");
                                showAlertOnFS(alert, event);
                                switchToStudents(event);
                            }
                        }

                    }


                }
            }
            else if(patternComma.matcher(addRateTextField.getText()).matches()
                    || patternComma2.matcher(addRateTextField.getText()).matches())
            {
                String value = addRateTextField.getText().replace(",",".");
                rate = Double.parseDouble(value);
                if(rate <= 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                    alert.setContentText("Rate shouldn't be equal or less than zero.");
                    showAlertOnFS(alert, event);
                }
                else{
                    if (addHoursWeeklyTextField.getText().isEmpty()) {
                        student = addStudentToDatabase(name, rate);
                        if (student == null) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                            alert.setContentText("Please, enter all required data do add a student succesfully.");
                            showAlertOnFS(alert, event);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("SUCCESS");
                            alert.setContentText("Student is added correctly. :)");
                            showAlertOnFS(alert, event);
                            switchToStudents(event);
                        }
                    } else {
                        if(patternComma.matcher(addHoursWeeklyTextField.getText()).matches() || patternComma2.matcher(addHoursWeeklyTextField.getText()).matches()){
                            Double hoursWeekly = Double.parseDouble(addHoursWeeklyTextField.getText().replace(",","."));
                            student = addStudentToDatabase(name, rate, hoursWeekly);
                            if (student == null) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                                alert.setContentText("Please, enter all required data do add a student succesfully.");
                                showAlertOnFS(alert, event);
                            } else {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("SUCCESS");
                                alert.setContentText("Student is added correctly. :)");
                                showAlertOnFS(alert, event);
                                switchToStudents(event);
                            }
                        }
                        else{
                            Double hoursWeekly = Double.parseDouble(addHoursWeeklyTextField.getText());
                            student = addStudentToDatabase(name, rate, hoursWeekly);
                            if (student == null) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                                alert.setContentText("Please, enter all required data do add a student succesfully.");
                                showAlertOnFS(alert, event);
                            } else {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("SUCCESS");
                                alert.setContentText("Student is added correctly. :)");
                                showAlertOnFS(alert, event);
                                switchToStudents(event);
                            }
                        }
                    }


                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD STUDENT");
                alert.setContentText("Please, enter rate and make it look like currency (but without zl). Also keep it under 1000.00 (and obviously more than zero)");
                showAlertOnFS(alert, event);
            }
        }

    }
    private Student addStudentToDatabase(String name, Double rate) {
        student = null;

        Pattern pattern2 = Pattern.compile("\\d{1,3}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,3}");
        if (pattern2.matcher(String.valueOf(rate)).matches()) {
            String temp = String.valueOf(rate);
            temp = temp + "0";
            rate = Double.parseDouble(temp);
        }
        if (pattern3.matcher(String.valueOf(rate)).matches()) {
            String temp = String.valueOf(rate);
            temp = temp + ".00";
            rate = Double.parseDouble(temp);
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "INSERT INTO students (name, rate) " +
                    "VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, rate);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                student = new Student(name, String.valueOf(rate));
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (student != null) {
            System.out.print("Student added to database...");
        } else {
            System.out.println("Adding student to database failed...");
        }


        return student;


    }
    private Student addStudentToDatabase(String name, Double rate, Double hoursWeekly) {
        student = null;

        Pattern pattern2 = Pattern.compile("\\d{1,3}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,3}");
        if (pattern2.matcher(String.valueOf(rate)).matches()) {
            String temp = String.valueOf(rate);
            temp = temp + "0";
            rate = Double.parseDouble(temp);
        }
        if (pattern3.matcher(String.valueOf(rate)).matches()) {
            String temp = String.valueOf(rate);
            temp = temp + ".00";
            rate = Double.parseDouble(temp);
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "INSERT INTO students (name, rate, hoursWeekly) " +
                    "VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, rate);
            preparedStatement.setDouble(3, hoursWeekly);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                student = new Student(name, String.valueOf(rate));
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (student != null) {
            System.out.print("Student added to database...");
        } else {
            System.out.println("Adding student to database failed...");
        }


        return student;


    }
    public void removeStudent(ActionEvent event) throws IOException {
        if(removeStudentChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO REMOVE STUDENT");
            alert.setContentText("Please, choose student to delete.");
            showAlertOnFS(alert, event);
        }
        else{
            String reading = removeStudentChoiceBox.getValue();
            if (reading == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO REMOVE STUDENT");
                alert.setContentText("Reading was null.");
                showAlertOnFS(alert, event);
            }
            String[] words = reading.split(" ");
            List<String> lessons1 = List.of(words);
            int last = words.length - 1;
            List<String> nameList = lessons1.subList(0, last);
            String name = "";
            for (int i = 0; i < nameList.size(); i++) {
                name += nameList.get(i) + " ";
            }
            name = name.trim();
            Double rate = Double.parseDouble(words[last]);

            if (name.isEmpty() || words[last].isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO REMOVE STUDENT");
                alert.setContentText("Values are empty (something went wrong with converting).");
                showAlertOnFS(alert, event);
            }

            student = removeStudentFromDatabase(name, rate);
            if (student != null) {
                last = words.length - 2;
                nameList = lessons1.subList(0, last);
                name = "";
                for (int i = 0; i < nameList.size(); i++) {
                    name += nameList.get(i) + " ";
                }
                name = name.trim();
                rate = Double.parseDouble(words[last]);
                Double hoursWeekly = Double.parseDouble(words[last + 1]);

                if (name.isEmpty() || words[last].isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO REMOVE STUDENT");
                    alert.setContentText("Values are empty (something went wrong with converting).");
                    showAlertOnFS(alert, event);
                }

                student = removeStudentFromDatabase(name, rate, hoursWeekly);
                if (student == null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("SUCCESS");
                    alert.setContentText("Student is removed correctly. :)");
                    showAlertOnFS(alert, event);
                    switchToStudents(event);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO REMOVE STUDENT");
                    alert.setContentText("RemoveStudentFromDatabase failed.");
                    showAlertOnFS(alert, event);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("SUCCESS");
                alert.setContentText("Student is removed correctly. :)");
                showAlertOnFS(alert, event);
                switchToStudents(event);
            }

        }

    }
    private Student removeStudentFromDatabase(String name, Double rate) {
        student = null;

        int deletedRows = 0;

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "DELETE FROM students " +
                    "WHERE name LIKE ? AND rate = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, rate);

            deletedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (deletedRows > 0) {
            System.out.print("Student removed from database...");
        } else {
            System.out.println("Removing student from database failed...");
            return new Student("", "0.0");
        }

        return student;


    }
    private Student removeStudentFromDatabase(String name, Double rate, Double hoursWeekly) {
        student = null;

        int deletedRows = 0;

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "DELETE FROM students " +
                    "WHERE name LIKE ? AND rate = ? AND hoursWeekly = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, rate);
            preparedStatement.setDouble(3, hoursWeekly);

            deletedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (deletedRows > 0) {
            System.out.print("Student removed from database...");
        } else {
            System.out.println("Removing student from database failed...");
            return new Student("", "0.0");
        }

        return student;


    }
    public void editStudent(ActionEvent event) throws IOException {
        Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{2}");
        Pattern pattern2 = Pattern.compile("\\d{1,3}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,3}");

        Pattern patternComma = Pattern.compile("\\d{1,3},\\d{2}");
        Pattern patternComma2 = Pattern.compile("\\d{1,3},\\d{1}");

        if(editNameTextField.getText().isEmpty() && editRateTextField.getText().isEmpty()
                && editStudentChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
            alert.setContentText("Please, enter all required data to edit student successfully.");
            showAlertOnFS(alert, event);
        }
        else if(editStudentChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
            alert.setContentText("Please, choose student to edit successfully.");
            showAlertOnFS(alert, event);
        }
        else if(editNameTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
            alert.setContentText("Please, enter name to edit student successfully.");
            showAlertOnFS(alert, event);
        }
        else if(editRateTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
            alert.setContentText("Please, enter rate to edit student successfully.");
            showAlertOnFS(alert, event);
        }
        else{
            String reading = editStudentChoiceBox.getValue();
            if (reading == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
                alert.setContentText("Reading was null.");
                showAlertOnFS(alert, event);
            }

            if (pattern.matcher(editRateTextField.getText()).matches() || pattern2.matcher(editRateTextField.getText()).matches() || pattern3.matcher(editRateTextField.getText()).matches()) {
                String[] words = reading.split(" ");
                List<String> lessons1 = List.of(words);
                int last = words.length - 2;
                List<String> nameList = lessons1.subList(0, last);
                String name = "";
                for (int i = 0; i < nameList.size(); i++) {
                    name += nameList.get(i) + " ";
                }
                name = name.trim();
                if (editNameTextField.getText().isEmpty() || words[last].isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
                    alert.setContentText("Values are empty (probably converting went wrong.");
                    showAlertOnFS(alert, event);
                }
                try {
                    Double rate = Double.parseDouble(words[last]);
                    Double hoursWeekly = Double.parseDouble(words[last + 1]);
                    String name2 = editNameTextField.getText();
                    Double rate2 = Double.parseDouble(editRateTextField.getText());
                    if (editHoursWeeklyTextField.getText().isEmpty()) {
                        student = editStudentInDatabase(name, rate, hoursWeekly, name2, rate2, 0.0);
                    } else {
                        if(patternComma.matcher(editHoursWeeklyTextField.getText()).matches() || patternComma2.matcher(editHoursWeeklyTextField.getText()).matches()){
                            Double hoursWeekly2 = Double.parseDouble(editHoursWeeklyTextField.getText().replace(",","."));
                            student = editStudentInDatabase(name, rate, hoursWeekly, name2, rate2, hoursWeekly2);
                        }
                        else{
                            Double hoursWeekly2 = Double.parseDouble(editHoursWeeklyTextField.getText());
                            student = editStudentInDatabase(name, rate, hoursWeekly, name2, rate2, hoursWeekly2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    student = new Student("", "0.0");
                }

                if(student!= null){
                    words = reading.split(" ");
                    lessons1 = List.of(words);
                    last = words.length - 1;
                    nameList = lessons1.subList(0, last);
                    name = "";
                    for (int i = 0; i < nameList.size(); i++) {
                        name += nameList.get(i) + " ";
                    }
                    name = name.trim();
                    try {
                        Double rate = Double.parseDouble(words[last]);
                        String name2 = editNameTextField.getText();
                        Double rate2 = Double.parseDouble(editRateTextField.getText());
                        if (editHoursWeeklyTextField.getText().isEmpty()) {
                            student = editStudentInDatabase(name, rate, name2, rate2, 0.0);
                        } else {
                            if(patternComma.matcher(editHoursWeeklyTextField.getText()).matches() || patternComma2.matcher(editHoursWeeklyTextField.getText()).matches()){
                                Double hoursWeekly2 = Double.parseDouble(editHoursWeeklyTextField.getText().replace(",","."));
                                student = editStudentInDatabase(name, rate, name2, rate2, hoursWeekly2);
                            }
                            else{
                                Double hoursWeekly2 = Double.parseDouble(editHoursWeeklyTextField.getText());
                                student = editStudentInDatabase(name, rate, name2, rate2, hoursWeekly2);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (student != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
                    alert.setContentText("Editing in database failed.");
                    showAlertOnFS(alert, event);
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("SUCCESS");
                    alert.setContentText("Student is edited correctly. :)");
                    showAlertOnFS(alert, event);
                    switchToStudents(event);

                }

            }
            if(patternComma.matcher(editRateTextField.getText()).matches() || patternComma2.matcher(editRateTextField.getText()).matches()){
                String[] words = reading.split(" ");
                List<String> lessons1 = List.of(words);
                int last = words.length - 2;
                List<String> nameList = lessons1.subList(0, last);
                String name = "";
                for (int i = 0; i < nameList.size(); i++) {
                    name += nameList.get(i) + " ";
                }
                name = name.trim();
                if (editNameTextField.getText().isEmpty() || words[last].isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
                    alert.setContentText("Values are empty (probably converting went wrong.");
                    showAlertOnFS(alert, event);
                }
                try {
                    Double rate = Double.parseDouble(words[last]);
                    Double hoursWeekly = Double.parseDouble(words[last + 1]);
                    String name2 = editNameTextField.getText();
                    Double rate2 = Double.parseDouble(editRateTextField.getText().replace(",","."));
                    if (editHoursWeeklyTextField.getText().isEmpty()) {
                        student = editStudentInDatabase(name, rate, hoursWeekly, name2, rate2, 0.0);
                    } else {
                        if(patternComma.matcher(editHoursWeeklyTextField.getText()).matches() || patternComma2.matcher(editHoursWeeklyTextField.getText()).matches()){
                            Double hoursWeekly2 = Double.parseDouble(editHoursWeeklyTextField.getText().replace(",","."));
                            student = editStudentInDatabase(name, rate, hoursWeekly, name2, rate2, hoursWeekly2);
                        }
                        else{
                            Double hoursWeekly2 = Double.parseDouble(editHoursWeeklyTextField.getText());
                            student = editStudentInDatabase(name, rate, hoursWeekly, name2, rate2, hoursWeekly2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    student = new Student("", "0.0");
                }

                if(student!= null){
                    words = reading.split(" ");
                    lessons1 = List.of(words);
                    last = words.length - 1;
                    nameList = lessons1.subList(0, last);
                    name = "";
                    for (int i = 0; i < nameList.size(); i++) {
                        name += nameList.get(i) + " ";
                    }
                    name = name.trim();
                    try {
                        Double rate = Double.parseDouble(words[last]);
                        String name2 = editNameTextField.getText();
                        Double rate2 = Double.parseDouble(editRateTextField.getText().replace(",","."));
                        if (editHoursWeeklyTextField.getText().isEmpty()) {
                            student = editStudentInDatabase(name, rate, name2, rate2, 0.0);
                        } else {
                            if(patternComma.matcher(editHoursWeeklyTextField.getText()).matches() || patternComma2.matcher(editHoursWeeklyTextField.getText()).matches()){
                                Double hoursWeekly2 = Double.parseDouble(editHoursWeeklyTextField.getText().replace(",","."));
                                student = editStudentInDatabase(name, rate, name2, rate2, hoursWeekly2);
                            }
                            else{
                                Double hoursWeekly2 = Double.parseDouble(editHoursWeeklyTextField.getText());
                                student = editStudentInDatabase(name, rate, name2, rate2, hoursWeekly2);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (student != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
                    alert.setContentText("Editing in database failed.");
                    showAlertOnFS(alert, event);
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("SUCCESS");
                    alert.setContentText("Student is edited correctly. :)");
                    showAlertOnFS(alert, event);
                    switchToStudents(event);

                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO EDIT STUDENT");
                alert.setContentText("Please, enter rate (greater than zero) to edit student successfully.");
                showAlertOnFS(alert, event);
            }
        }

    }
    private Student editStudentInDatabase(String name, Double rate, Double hoursWeekly, String name2, Double rate2, Double hoursWeekly2) {
        student = null;

        int editedRows = 0;
        Pattern pattern2 = Pattern.compile("\\d{1,3}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,3}");
        if (pattern2.matcher(String.valueOf(rate2)).matches()) {
            String temp = String.valueOf(rate2);
            temp = temp + "0";
            rate2 = Double.parseDouble(temp);
        }
        if (pattern3.matcher(String.valueOf(rate2)).matches()) {
            String temp = String.valueOf(rate2);
            temp = temp + ".00";
            rate2 = Double.parseDouble(temp);
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "UPDATE students " +
                    "SET name = ?, rate = ?, hoursWeekly = ? " +
                    "WHERE name LIKE ? AND rate LIKE ? AND hoursWeekly LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name2);
            preparedStatement.setDouble(2, rate2);
            preparedStatement.setDouble(3, hoursWeekly2);
            preparedStatement.setString(4, name);
            preparedStatement.setDouble(5, rate);
            preparedStatement.setDouble(6, hoursWeekly);

            editedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "UPDATE students " +
                    "SET name = ?, rate = ?, hoursWeekly = ? " +
                    "WHERE name LIKE ? AND rate = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name2);
            preparedStatement.setDouble(2, rate2);
            preparedStatement.setDouble(3, hoursWeekly2);
            preparedStatement.setString(4, name);
            preparedStatement.setDouble(5, rate);

            editedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (editedRows > 0) {
            System.out.print("Student edited in database...");
        } else {
            System.out.println("Editing student in database failed...");
            return new Student(name2, String.valueOf(rate2));
        }

        return student;


    }
    private Student editStudentInDatabase(String name, Double rate, String name2, Double rate2, Double hoursWeekly2) {
        student = null;

        int editedRows = 0;
        Pattern pattern2 = Pattern.compile("\\d{1,3}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,3}");
        if (pattern2.matcher(String.valueOf(rate2)).matches()) {
            String temp = String.valueOf(rate2);
            temp = temp + "0";
            rate2 = Double.parseDouble(temp);
        }
        if (pattern3.matcher(String.valueOf(rate2)).matches()) {
            String temp = String.valueOf(rate2);
            temp = temp + ".00";
            rate2 = Double.parseDouble(temp);
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "UPDATE students " +
                    "SET name = ?, rate = ?, hoursWeekly = ? " +
                    "WHERE name LIKE ? AND rate LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name2);
            preparedStatement.setDouble(2, rate2);
            preparedStatement.setDouble(3, hoursWeekly2);
            preparedStatement.setString(4, name);
            preparedStatement.setDouble(5, rate);

            editedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "UPDATE students " +
                    "SET name = ?, rate = ?, hoursWeekly = ? " +
                    "WHERE name LIKE ? AND rate = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name2);
            preparedStatement.setDouble(2, rate2);
            preparedStatement.setDouble(3, hoursWeekly2);
            preparedStatement.setString(4, name);
            preparedStatement.setDouble(5, rate);

            editedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (editedRows > 0) {
            System.out.print("Student edited in database...");
        } else {
            System.out.println("Editing student in database failed...");
            return new Student(name2, String.valueOf(rate2));
        }

        return student;


    }
    public String savingsToDeposit() {
        /*
            returning data stated above to view as a statistic in textField
         */
        String sqlQuery = "SELECT SUM(amount) * 0.75 AS amount FROM payments;";

        Double value = 0.0;

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                value = (resultSet.getDouble("amount"));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Double result = (value - Double.parseDouble(savedSavings()));
        return result.toString();

    }
    public String savedSavings() {
        /*
            returning data stated above to view as a statistic in textField
         */
        String sqlQuery = "SELECT SUM(quantity) AS quantity FROM savings;";
        Double value = 0.0;

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                value = (resultSet.getDouble("quantity"));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value.toString();


    }

    public void switchToMainPage(ActionEvent event) throws IOException {
        switchScenes("fxml/mainPage.fxml", event);
    }

    public void switchToLessons(ActionEvent event) throws IOException {
        switchScenes("fxml/lessons.fxml", event);
    }

    public void switchToStudents(ActionEvent event) throws IOException {
        switchScenes("fxml/students.fxml", event);
    }

    public void switchToPayments(ActionEvent event) throws IOException {
        switchScenes("fxml/payments.fxml", event);
    }

    public void switchToSavings(ActionEvent event) throws IOException {
        /*
            here the method switchScenes is not used because of components that needs to be
            handled differently to be initialized like progress bar or label above it
         */
        fxmlLoader = new FXMLLoader(getClass().getResource("fxml/savings.fxml"));
        Parent root = fxmlLoader.load();
        SavingsLogic controller = fxmlLoader.getController();
        controller.getToDepositTextField().setText(savingsToDeposit());
        controller.getSavedTextField().setText(savedSavings());
        double progress = (Double.parseDouble(controller.getSavedTextField().getText()) / 10000.0);
        controller.getProgressBar().setProgress(progress);
        controller.getPercentageLabel().setText(controller.getPercentageLabel().getText() + " " + Math.round(progress * 100) + "%");
        controller.getToDepositTextField().setText(savingsToDeposit() + " zl");
        controller.getSavedTextField().setText(savedSavings() + " zl");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean fs = stage.isFullScreen();
        stage.close();
        stage.setScene(new Scene(root));
        stage.setFullScreen(fs);
        stage.show();
    }

    public void switchToCalendar(ActionEvent event) throws IOException {
        switchScenes("fxml/calendar.fxml", event);
    }

    //template method for switching scenes
    public void switchScenes(String fxml, ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean fs = stage.isFullScreen();
        scene = new Scene(fxmlLoader.load());
        stage.close();
        stage.setScene(scene);
        stage.setFullScreen(fs);
        stage.show();
    }

    //template method for generating alerts that pop up on top of the page even when fullscreen is on
    private void showAlertOnFS(Alert alert, Event event) throws IOException {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        boolean fs = stage.isFullScreen();
        scene = ((Node) event.getSource()).getScene();
        stage.setScene(scene);
        stage.setFullScreen(fs);
        alert.initOwner(stage);
        alert.showAndWait();
    }

}
