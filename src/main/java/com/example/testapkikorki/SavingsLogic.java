package com.example.testapkikorki;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class SavingsLogic {

    @FXML
    private TextField toDepositTextField, savedTextField, addTextField;

    @FXML
    private Label percentageLabel;

    @FXML
    private DatePicker fromDateDatePicker, toDateDatePicker, dateDatePicker;

    @FXML
    private ProgressBar progressBar;
    @FXML
    public BorderPane savingsBorderPane;


    // coordinates used for moving application across the screen
    private double x = 0, y = 0;
    private Stage stage = new Stage();
    private FXMLLoader fxmlLoader;
    private Scene scene;
    final String DB_URL = "jdbc:mysql://localhost:3306/myapp1", USERNAME = "root", PASSWORD = "root";
    public Saving saving;



    //method initializing all essential components at the beginning of running lessons page
    @FXML
    public void initialize() {
        progressBar.setProgress(0.0);
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
    public void movingWhenDragged(MouseEvent event) {
        Stage stage = (Stage) savingsBorderPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void movingWhenPressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }


    public TextField getToDepositTextField() {
        return toDepositTextField;
    }
    public TextField getSavedTextField() {
        return savedTextField;
    }
    public Label getPercentageLabel() {
        return percentageLabel;
    }
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    public void showSavingsList(Event event) throws IOException {
        Stage stage2 = new Stage();
        FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("fxml/savingsInTable.fxml"));
        Scene scene2 = new Scene(fxmlLoader2.load());
        stage2.initOwner((Stage) ((Node) event.getSource()).getScene().getWindow());
        stage2.setScene(scene2);
        InputStream inputStream = HelloApplication.class.getResourceAsStream("/png/logo.png");
        if (inputStream != null) {
            stage2.getIcons().add(new Image(inputStream));
        }
        stage2.setTitle("List of your savings!! :)");
        stage2.show();
    }
    public void checkByDates(ActionEvent event) throws IOException {
        LocalDate fromDate = fromDateDatePicker.getValue();
        LocalDate toDate = toDateDatePicker.getValue();
        String answer = "";

        if (toDate != null || fromDate != null) {
            if (fromDate == null) {
                fromDate = LocalDate.of(2023, 12, 1);
            }
            if (toDate == null) {
                toDate = LocalDate.now();
            }
            if (ChronoUnit.DAYS.between(fromDate, toDate) < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setContentText("Your data \"from date\" is later than your \"to date\"");
                showAlertOnFS(alert, event);
            }
            if (ChronoUnit.DAYS.between(fromDate, toDate) == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                ButtonType option1 = new ButtonType("OK");
                ButtonType cancel = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
                DialogPane dialogPane = new DialogPane();
                dialogPane.getButtonTypes().addAll(option1, cancel);
                alert.setDialogPane(dialogPane);
                alert.setTitle("ARE YOU SURE?");
                alert.setContentText("Your data \"from date\" is the same as your \"to date\"");
                showAlertOnFS(alert, event);
                answer = alert.getResult().getText();
            }
            if (ChronoUnit.DAYS.between(fromDate, toDate) > 0) {
                answer = "OK";
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("NO FILTER");
            alert.setContentText("Your savings are filtered the same way as they were before.");
            showAlertOnFS(alert, event);
            switchToSavings(event);
        }
        double toDeposit = 0, savedMoney = 0;

        if (fromDate != null && toDate != null && answer.equals("OK")) {
            toDeposit = toDepositBasedOnDateDatabase(fromDate.toString(), toDate.toString());
            savedMoney = savedMoneyBasedOnDateDatabase(fromDate.toString(), toDate.toString());
            toDepositTextField.setText(String.valueOf(toDeposit));
            savedTextField.setText(String.valueOf(savedMoney));
        }
        double progress = (Double.parseDouble(savedTextField.getText()) / 10000.0);
        progressBar.setProgress(progress);
        percentageLabel.setText("Percentage of money saved out of 10 000zł :" + " " + Math.round(progress * 100) + "%");


    }
    private double savedMoneyBasedOnDateDatabase(String fromDate, String toDate) {

        int executedCommands = 0;
        Double value = 0.0;

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "SELECT SUM(amount) * 0.25 AS amount FROM payments " +
                    "WHERE date BETWEEN ? AND ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, fromDate);
            preparedStatement.setString(2, toDate);
            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                value = (resultSet.getDouble("amount"));
                executedCommands++;
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (executedCommands > 0) {
            System.out.print("Saved money filter worked... ");
        } else {
            System.out.println("Saved money filter in database failed...");
        }
        return value;

    }
    private double toDepositBasedOnDateDatabase(String fromDate, String toDate) {

        Double value = 0.0;
        int executedCommands = 0;

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "SELECT SUM(amount) * 0.75 AS amount FROM payments " +
                    "WHERE date BETWEEN ? AND ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, fromDate);
            preparedStatement.setString(2, toDate);
            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                value = (resultSet.getDouble("amount"));
                executedCommands++;
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (executedCommands > 0) {
            System.out.print("To Deposit filter worked... ");
        } else {
            System.out.println("To deposit filter in database failed...");
        }
        return value;

    }
    public void addSaving(ActionEvent event) throws IOException {
        Pattern patternQuantity1 = Pattern.compile("\\d{1,4}\\.\\d{2}");
        Pattern patternQuantity2 = Pattern.compile("\\d{1,4}\\.\\d{1}");
        Pattern patternQuantity3 = Pattern.compile("\\d{1,4}");

        Pattern patternComma = Pattern.compile("\\d{1,3},\\d{2}");
        Pattern patternComma2 = Pattern.compile("\\d{1,3},\\d{1}");

        if (addTextField.getText().isEmpty() && dateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD SAVING");
            alert.setContentText("Please, enter required data do submit saving succesfully.");
            showAlertOnFS(alert, event);
        } else if (addTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD SAVING");
            alert.setContentText("Please, enter quantity to submit saving succesfully.");
            showAlertOnFS(alert, event);
        } else if (dateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD SAVING");
            alert.setContentText("Please, choose date to submit saving succesfully.");
            showAlertOnFS(alert, event);
        } else {
            if (patternQuantity1.matcher(addTextField.getText()).matches()
                    || patternQuantity2.matcher(addTextField.getText()).matches()
                    || patternQuantity3.matcher(addTextField.getText()).matches()) {
                if(Double.parseDouble(addTextField.getText()) <= 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD SAVING");
                    alert.setContentText("The amount of saving shouldn't be equal or less than zero.");
                    showAlertOnFS(alert, event);
                    switchToPayments(event);
                }
                else{
                    String date = dateDatePicker.getValue().toString();
                    String quantity = addTextField.getText();
                    saving = addSavingToDatabase(quantity, date);
                    if (saving != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("SUCCESS");
                        alert.setContentText("Saving is submitted. :)");
                        showAlertOnFS(alert, event);
                        switchToSavings(event);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO ADD SAVING");
                        alert.setContentText("Please, enter required data to submit saving succesfully.");
                        showAlertOnFS(alert, event);
                    }
                }
            }
            else if(patternComma.matcher(addTextField.getText()).matches()
                    || patternComma2.matcher(addTextField.getText()).matches()){
                if(Double.parseDouble(addTextField.getText().replace(",",".")) <= 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD SAVING");
                    alert.setContentText("The amount of saving shouldn't be equal or less than zero.");
                    showAlertOnFS(alert, event);
                    switchToPayments(event);
                }
                else{
                    String date = dateDatePicker.getValue().toString();
                    String quantity = addTextField.getText().replace(",",".");
                    saving = addSavingToDatabase(quantity, date);
                    if (saving != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("SUCCESS");
                        alert.setContentText("Saving is submitted. :)");
                        showAlertOnFS(alert, event);
                        switchToSavings(event);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO ADD SAVING");
                        alert.setContentText("Please, enter required data to submit saving succesfully.");
                        showAlertOnFS(alert, event);
                    }
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD SAVING");
                alert.setContentText("Please, enter saving's amount as floating-point number like in \nthe pattern (but without zl/h) and keep it between \n0.00 and 10000.00");
                showAlertOnFS(alert, event);
            }

        }

    }
    private Saving addSavingToDatabase(String quantity, String date) {
        saving = null;

        Pattern patternQuantity2 = Pattern.compile("\\d{1,4}\\.\\d{1}");
        Pattern patternQuantity3 = Pattern.compile("\\d{1,4}");

        if (patternQuantity2.matcher(quantity).matches()) {
            quantity = quantity + "0";
        }
        if (patternQuantity3.matcher(quantity).matches()) {
            quantity = quantity + ".00";
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "INSERT INTO savings (quantity, date) " +
                    "VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setDouble(1, Double.parseDouble(quantity));
            preparedStatement.setString(2, date);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                saving = new Saving(quantity, date);
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (saving != null) {
            System.out.print("Saving added to database...");
        } else {
            System.out.println("Adding saving to database failed...");
        }

        return saving;


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
