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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class LessonsLogic {

    @FXML
    private TextField editHoursTextField, addHoursTextField, addTimeTextField, editTimeTextField;
    @FXML
    private DatePicker editDateDatePicker, addDateDatePicker;
    @FXML
    private ChoiceBox<String> addNameChoiceBox, addTypeChoiceBox, editTypeChoiceBox
            , removeLessonChoiceBox, editLessonChoiceBox, editNameChoiceBox;


    //all essential information to connect to database
    final String DB_URL = "jdbc:mysql://localhost:3306/myapp1", USERNAME = "root", PASSWORD = "root";
    private Stage stage;
    private FXMLLoader fxmlLoader;
    private Scene scene;
    ObservableList<String> typesList = FXCollections.observableArrayList("online", "stationary")
            , namesList = getNames(), lessonsList = getLessons();
    public Lesson lesson;
    public BorderPane lessonsBorderPane;

    // coordinates used for moving application across the screen
    private double x = 0, y = 0;



    //method initializing all essential components at the beginning of running lessons page
    @FXML
    private void initialize() {
        addNameChoiceBox.setItems(namesList);
        editNameChoiceBox.setItems(namesList);
        removeLessonChoiceBox.setItems(lessonsList);
        editLessonChoiceBox.setItems(lessonsList);
        addTypeChoiceBox.setItems(typesList);
        editTypeChoiceBox.setItems(typesList);
    }
    @FXML
    public void minimiseWindow(MouseEvent e) {
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setIconified(true);
    }
    @FXML
    public void maximizeWindow(MouseEvent e) {
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
    public void movingWhenDragged(MouseEvent event) {
        Stage stage = (Stage) lessonsBorderPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void movingWhenPressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }


    //method used for generating list of lessons in separate window
    public void showLessonsList(Event event) throws IOException {
        Stage stage2 = new Stage();
        FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("fxml/lessonsInTable.fxml"));
        Scene scene2 = new Scene(fxmlLoader2.load());
        stage2.initOwner((Stage) ((Node) event.getSource()).getScene().getWindow());
        stage2.setScene(scene2);
        InputStream inputStream = HelloApplication.class.getResourceAsStream("/png/logo.png");
        if (inputStream != null) {
            stage2.getIcons().add(new Image(inputStream));
        }
        stage2.setTitle("List of your lessons :)");
        stage2.show();
    }

    // methods getLessons and getNames are for providing list of objects to appear in choice boxes
    public ObservableList<String> getLessons() {
        ObservableList<String> lessonsList = FXCollections.observableArrayList();
        String sqlQuery = "SELECT * FROM lessons;";

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String value = "";
                value += resultSet.getString("name") + " ";
                value += resultSet.getString("date") + " ";
                Double hours = resultSet.getDouble("hours");
                value += hours.toString() + " ";
                value += resultSet.getString("time") + " ";
                value += resultSet.getString("typeOfLesson") + " ";
                lessonsList.add(value);
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lessonsList;

    }

    public ObservableList<String> getNames() {
        ObservableList<String> namesList = FXCollections.observableArrayList();
        String sqlQuery = "SELECT name FROM students";

        namesList.clear();

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                namesList.add(resultSet.getString("name"));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return namesList;

    }
    public void addLesson(ActionEvent event) throws IOException {

        Pattern patternHours1 = Pattern.compile("\\d{1}\\.\\d{2}");
        Pattern patternHours2 = Pattern.compile("\\d{1}\\.\\d{1}");
        Pattern patternHours3 = Pattern.compile("\\d{1}");

        Pattern patternComma = Pattern.compile("\\d{1},\\d{2}");
        Pattern patternComma2 = Pattern.compile("\\d{1},\\d{1}");

        Pattern patternTime = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

        if(addNameChoiceBox.getValue() == null && addTypeChoiceBox.getValue() == null
                && addTimeTextField.getText().isEmpty() && addHoursTextField.getText().isEmpty()
                && addDateDatePicker.getValue() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
            alert.setContentText("Please, enter all required data to add a lesson succesfully.");
            showAlertOnFS(alert, event);
        }
        else{
            if (addNameChoiceBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                alert.setContentText("Please, choose name to add a lesson succesfully.");
                showAlertOnFS(alert, event);
            }
            else if (addDateDatePicker.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                alert.setContentText("Please, choose date to add a lesson succesfully.");
                showAlertOnFS(alert, event);
            }
            else if (addHoursTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                alert.setContentText("Please, enter how many hours to add a lesson succesfully.");
                showAlertOnFS(alert, event);
            }
            else if (addTimeTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                alert.setContentText("Please, enter time to add a lesson succesfully.");
                showAlertOnFS(alert, event);
            }
            else if (addTypeChoiceBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                alert.setContentText("Please, choose type to add a lesson succesfully.");
                showAlertOnFS(alert, event);
            } else {
                String name = addNameChoiceBox.getValue();
                LocalDate date = addDateDatePicker.getValue();
                String hours = addHoursTextField.getText();
                String time = addTimeTextField.getText();
                String typeOfLesson = addTypeChoiceBox.getValue();
                if (patternHours1.matcher(hours).matches() || patternHours2.matcher(hours).matches()
                        || patternHours3.matcher(hours).matches()) {
                    if (patternTime.matcher(time).matches()) {

                        lesson = addLessonToDatabase(name, date.toString(), hours, time, typeOfLesson);
                        if (lesson != null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("SUCCESS");
                            alert.setContentText("Lesson is added correctly. :)");
                            showAlertOnFS(alert, event);
                            switchToLessons(event);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                            alert.setContentText("addLessonToDatabase failed...");
                            showAlertOnFS(alert, event);
                        }


                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                        alert.setContentText("Please, enter time like European time pattern");
                        showAlertOnFS(alert, event);
                    }
                }
                else if(patternComma.matcher(hours).matches() || patternComma2.matcher(hours).matches()){
                    if (patternTime.matcher(time).matches()) {

                        lesson = addLessonToDatabase(name, date.toString(), hours.replace(",","."), time, typeOfLesson);
                        if (lesson != null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("SUCCESS");
                            alert.setContentText("Lesson is added correctly. :)");
                            showAlertOnFS(alert, event);
                            switchToLessons(event);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                            alert.setContentText("addLessonToDatabase failed...");
                            showAlertOnFS(alert, event);
                        }


                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                        alert.setContentText("Please, enter time like European time pattern");
                        showAlertOnFS(alert, event);
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                    alert.setContentText("Please, enter hours as floating-point number like in the pattern and keep it under 10.00");
                    showAlertOnFS(alert, event);
                }
            }
        }

    }
    private Lesson addLessonToDatabase(String name, String date, String hours, String time, String typeOfLesson) {
        lesson = null;

        Pattern patternHours3 = Pattern.compile("\\d{1}");
        if (patternHours3.matcher(hours).matches()) {
            hours = hours + ".00";
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "INSERT INTO lessons (name, date, hours, time, typeOfLesson) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, date);
            preparedStatement.setDouble(3, Double.parseDouble(hours));
            preparedStatement.setString(4, time);
            preparedStatement.setString(5, typeOfLesson);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                lesson = new Lesson(name, date, hours, time, typeOfLesson);
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lesson != null) {
            System.out.print("Lesson added to database...");
        } else {
            System.out.println("Adding lesson to database failed...");
        }

        return lesson;


    }
    public void removeLesson(ActionEvent event) throws IOException {
        if (removeLessonChoiceBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO REMOVE LESSON");
            alert.setContentText("Please, choose lesson to remove.");
            showAlertOnFS(alert, event);
        }
        else{
            String reading = removeLessonChoiceBox.getValue();
            String[] words = reading.split(" ");
            List<String> lessons1 = List.of(words);
            int last = words.length - 1;
            List<String> nameList = lessons1.subList(0, last - 3);
            String name = "";
            for (int i = 0; i < nameList.size(); i++) {
                name += nameList.get(i) + " ";
            }
            name = name.trim();
            String date = words[last - 3];
            String hours = words[last - 2];
            String time = words[last - 1];
            String typeOfLessons = words[last];

            if (name == null || date.isEmpty() || hours.isEmpty() || time.isEmpty() || typeOfLessons.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO REMOVE LESSON");
                alert.setContentText("Reading failed.");
                showAlertOnFS(alert, event);
            }

            lesson = removeLessonFromDatabase(name, date, hours, time, typeOfLessons);
            if (lesson == null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("SUCCESS");
                alert.setContentText("Lesson is removed correctly. :)");
                showAlertOnFS(alert, event);
                switchToLessons(event);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO REMOVE LESSON");
                alert.setContentText("RemoveLessonFromDatabase failed.");
                showAlertOnFS(alert, event);
            }
        }

    }
    private Lesson removeLessonFromDatabase(String name, String date, String hours, String time, String typeOfLessons) {
        lesson = null;

        int deletedRows = 0;

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "DELETE FROM lessons " +
                    "WHERE name LIKE ? AND date LIKE ? AND hours = ? AND time LIKE ? AND typeOfLesson LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, date);
            preparedStatement.setDouble(3, Double.parseDouble(hours));
            preparedStatement.setString(4, time);
            preparedStatement.setString(5, typeOfLessons);

            deletedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (deletedRows > 0) {
            System.out.print("Lesson removed from database...");
        } else {
            System.out.println("Removing lesson from database failed...");
            return new Lesson("", "", "", "", "");
        }

        return lesson;


    }
    public void editLesson(ActionEvent event) throws IOException {
        Pattern patternHours = Pattern.compile("\\d{1}\\.\\d{2}");
        Pattern patternHours2 = Pattern.compile("\\d{1}\\.\\d{1}");
        Pattern patternHours3 = Pattern.compile("\\d{1}");

        Pattern patternComma = Pattern.compile("\\d{1},\\d{2}");
        Pattern patternComma2 = Pattern.compile("\\d{1},\\d{1}");

        Pattern patternTime = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

        if(editLessonChoiceBox.getValue() == null && editNameChoiceBox.getValue() == null
                && editHoursTextField.getText().isEmpty() && editTimeTextField.getText().isEmpty()
                && editDateDatePicker.getValue() == null && editTypeChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
            alert.setContentText("Please, enter all required data to successfully edit a lesson");
            showAlertOnFS(alert, event);
        }
        else if(editLessonChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
            alert.setContentText("Choose lesson to edit.");
            showAlertOnFS(alert, event);
        }
        else if(editNameChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
            alert.setContentText("Choose name of the person to edit lesson successfully.");
            showAlertOnFS(alert, event);
        }
        else if(editDateDatePicker.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
            alert.setContentText("Choose date of the lesson.");
            showAlertOnFS(alert, event);
        }
        else if(editHoursTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
            alert.setContentText("Please, enter how many hours.");
            showAlertOnFS(alert, event);
        }
        else if(editTimeTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
            alert.setContentText("Please, enter time of the lesson.");
            showAlertOnFS(alert, event);
        }
        else if(editTypeChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
            alert.setContentText("Choose type of the lesson.");
            showAlertOnFS(alert, event);
        }
        else{
            String reading = editLessonChoiceBox.getValue();
            if (reading == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
                alert.setContentText("Reading was null.");
                showAlertOnFS(alert, event);
            }
            if (patternHours.matcher(editHoursTextField.getText()).matches() || patternHours2.matcher(editHoursTextField.getText()).matches()
                    || patternHours3.matcher(editHoursTextField.getText()).matches()) {
                if (patternTime.matcher(editTimeTextField.getText()).matches()) {
                    String[] words = reading.split(" ");
                    List<String> lessons1 = List.of(words);
                    int last = words.length - 1;
                    List<String> nameList = lessons1.subList(0, last - 3);
                    String name = "";
                    for (int i = 0; i < nameList.size(); i++) {
                        name += nameList.get(i) + " ";
                    }
                    name = name.trim();
                    String date = words[last - 3];
                    String hours = words[last - 2];
                    String time = words[last - 1];
                    String typeOfLesson = words[last];
                    String name2 = editNameChoiceBox.getValue();
                    String type2 = editTypeChoiceBox.getValue();
                    if (name2 == null || type2 == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
                        alert.setContentText("Please, enter all required data do edit a lesson succesfully.");
                        showAlertOnFS(alert, event);
                    }
                    String date2 = editDateDatePicker.getValue().toString();
                    String hours2 = editHoursTextField.getText();
                    String time2 = editTimeTextField.getText();
                    if (name.isEmpty() || date.isEmpty() || hours.isEmpty() || time.isEmpty() || typeOfLesson.isEmpty()
                            || name2.isEmpty() || date2.isEmpty() || hours2.isEmpty() || time2.isEmpty() || type2.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO EDIT LESSOn");
                        alert.setContentText("Some data was empty (probably reading)");
                        showAlertOnFS(alert, event);
                    } else {
                        lesson = editLessonInDatabase(name, date, hours, time, typeOfLesson, name2, date2, hours2, time2, type2);
                        if (lesson == null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("SUCCESS");
                            alert.setContentText("Lesson is edited correctly. :)");
                            showAlertOnFS(alert, event);
                            switchToLessons(event);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
                            alert.setContentText("EditLessonInDatabase failed.");
                            showAlertOnFS(alert, event);
                        }
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                    alert.setContentText("Please, enter time like European time pattern");
                    showAlertOnFS(alert, event);
                }
            }
            else if(patternComma.matcher(editHoursTextField.getText()).matches() || patternComma2.matcher(editHoursTextField.getText()).matches()){
                if (patternTime.matcher(editTimeTextField.getText()).matches()) {
                    String[] words = reading.split(" ");
                    List<String> lessons1 = List.of(words);
                    int last = words.length - 1;
                    List<String> nameList = lessons1.subList(0, last - 3);
                    String name = "";
                    for (int i = 0; i < nameList.size(); i++) {
                        name += nameList.get(i) + " ";
                    }
                    name = name.trim();
                    String date = words[last - 3];
                    String hours = words[last - 2];
                    String time = words[last - 1];
                    String typeOfLesson = words[last];
                    String name2 = editNameChoiceBox.getValue();
                    String type2 = editTypeChoiceBox.getValue();
                    if (name2 == null || type2 == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
                        alert.setContentText("Please, enter all required data do edit a lesson succesfully.");
                        showAlertOnFS(alert, event);
                    }
                    String date2 = editDateDatePicker.getValue().toString();
                    String hours2 = editHoursTextField.getText().replace(",",".");
                    String time2 = editTimeTextField.getText();
                    if (name.isEmpty() || date.isEmpty() || hours.isEmpty() || time.isEmpty() || typeOfLesson.isEmpty()
                            || name2.isEmpty() || date2.isEmpty() || hours2.isEmpty() || time2.isEmpty() || type2.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO EDIT LESSOn");
                        alert.setContentText("Some data was empty (probably reading)");
                        showAlertOnFS(alert, event);
                    } else {
                        lesson = editLessonInDatabase(name, date, hours, time, typeOfLesson, name2, date2, hours2, time2, type2);
                        if (lesson == null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("SUCCESS");
                            alert.setContentText("Lesson is edited correctly. :)");
                            showAlertOnFS(alert, event);
                            switchToLessons(event);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("FAILED ATTEMPT TO EDIT LESSON");
                            alert.setContentText("EditLessonInDatabase failed.");
                            showAlertOnFS(alert, event);
                        }
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                    alert.setContentText("Please, enter time like European time pattern");
                    showAlertOnFS(alert, event);
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD LESSON");
                alert.setContentText("Please, enter hours as floating-point number like in the pattern and keep it under 10.00");
                showAlertOnFS(alert, event);
            }
        }

    }
    private Lesson editLessonInDatabase(String name, String date, String hours, String time, String typeOfLesson, String name2, String date2, String hours2, String time2, String typeOfLesson2) {
        lesson = null;

        int editedRows = 0;
        Pattern patternHours3 = Pattern.compile("\\d{1}");
        if (patternHours3.matcher(hours2).matches()) {
            hours2 = hours2 + ".00";
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "UPDATE lessons " +
                    "SET name = ?, date = ?, hours = ?, time = ?, typeOfLesson = ? " +
                    "WHERE name LIKE ? AND date LIKE ? AND hours = ? AND time LIKE ? AND typeOfLesson LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name2);
            preparedStatement.setString(2, date2);
            preparedStatement.setDouble(3, Double.parseDouble(hours2));
            preparedStatement.setString(4, time2);
            preparedStatement.setString(5, typeOfLesson2);
            preparedStatement.setString(6, name);
            preparedStatement.setString(7, date);
            preparedStatement.setDouble(8, Double.parseDouble(hours));
            preparedStatement.setString(9, time);
            preparedStatement.setString(10, typeOfLesson);

            editedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (editedRows > 0) {
            System.out.print("Lesson edited in database...");
        } else {
            System.out.println("Editing lesson in database failed...");
            return new Lesson(name2, date2, hours2, time2, typeOfLesson2);
        }

        return lesson;
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
