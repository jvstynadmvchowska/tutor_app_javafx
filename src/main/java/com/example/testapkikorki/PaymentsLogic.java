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
import javafx.scene.control.*;
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

public class PaymentsLogic {

    @FXML
    private TextField addAmountTextField, editAmountTextField;

    @FXML
    private DatePicker addDateDatePicker, editDateDatePicker;

    @FXML
    private ChoiceBox<String> addNameChoiceBox, editChoiceBox, removeChoiceBox, editNameChoiceBox;


    //all essential information to connect to database
    final String DB_URL = "jdbc:mysql://localhost:3306/myapp1", USERNAME = "root", PASSWORD = "root";
    public BorderPane paymentsBorderPane;

    // coordinates used for moving application across the screen
    private double x = 0, y = 0;
    private Stage stage = new Stage();
    private FXMLLoader fxmlLoader;
    private Scene scene;
    ObservableList<String> paymentsList = getPayments(), namesList = getNames();

    public Payment payment;



    //method initializing all essential components at the beginning of running lessons page
    @FXML
    private void initialize() {
        addNameChoiceBox.setItems(namesList);
        editNameChoiceBox.setItems(namesList);
        editChoiceBox.setItems(paymentsList);
        removeChoiceBox.setItems(paymentsList);
    }

    /*
        methods movingWhenDragged and movingWhenPressed are used for moving the application across the screen
        when user grabs and moves it around with mouse click
     */
    @FXML
    public void movingWhenDragged(MouseEvent event) {
        Stage stage = (Stage) paymentsBorderPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void movingWhenPressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
    @FXML
    public void minimaliseWindow(MouseEvent e) {
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


    public void showPaymentsList(Event event) throws IOException {
        Stage stage2 = new Stage();
        FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("fxml/paymentsInTable.fxml"));
        Scene scene2 = new Scene(fxmlLoader2.load());
        stage2.initOwner((Stage) ((Node) event.getSource()).getScene().getWindow());
        stage2.setScene(scene2);
        InputStream inputStream = HelloApplication.class.getResourceAsStream("/png/logo.png");
        if (inputStream != null) {
            stage2.getIcons().add(new Image(inputStream));
        }
        stage2.setTitle("List of your payments!! :)");
        stage2.show();
    }

    public ObservableList<String> getPayments() {
        ObservableList<String> paymentsList = FXCollections.observableArrayList();
        String sqlQuery = "SELECT * FROM payments;";

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String value = "";
                value += resultSet.getString("name") + " ";
                value += resultSet.getString("date") + " ";
                Double amount = resultSet.getDouble("amount");
                value += amount.toString() + " ";
                paymentsList.add(value);
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return paymentsList;

    }

    public ObservableList<String> getNames() {
        ObservableList<String> namesList = FXCollections.observableArrayList();
        String sqlQuery = "SELECT name FROM students";

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
    public void addPayment(ActionEvent event) throws IOException {
        Pattern pattern1 = Pattern.compile("\\d{1,4}\\.\\d{2}");
        Pattern pattern2 = Pattern.compile("\\d{1,4}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,4}");

        Pattern patternComma = Pattern.compile("\\d{1,4},\\d{2}");
        Pattern patternComma2 = Pattern.compile("\\d{1,4},\\d{1}");

        if (addNameChoiceBox.getValue() == null && addDateDatePicker.getValue() == null
                && addAmountTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
            alert.setContentText("Please, enter all required data do add a payment succesfully.");
            showAlertOnFS(alert, event);
        } else if (addNameChoiceBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
            alert.setContentText("Please, choose name to add a payment succesfully.");
            showAlertOnFS(alert, event);
        } else if (addDateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
            alert.setContentText("Please, choose date to add a payment succesfully.");
            showAlertOnFS(alert, event);
        } else if (addAmountTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
            alert.setContentText("Please, enter amount to add a payment succesfully.");
            showAlertOnFS(alert, event);
        } else {
            String name = addNameChoiceBox.getValue();
            LocalDate date = addDateDatePicker.getValue();
            Double amount;

            if (pattern1.matcher(addAmountTextField.getText()).matches()
                    || pattern2.matcher(addAmountTextField.getText()).matches()
                    || pattern3.matcher(addAmountTextField.getText()).matches()) {
                if(Double.parseDouble(addAmountTextField.getText()) <= 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
                    alert.setContentText("The amount shouldn't be equal or less than zero.");
                    showAlertOnFS(alert, event);
                    switchToPayments(event);
                }
                else{
                    amount = Double.parseDouble(addAmountTextField.getText());
                    payment = addPaymentToDatabase(name, date.toString(), amount);
                    if (payment != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("SUCCESS");
                        alert.setContentText("Payment is added correctly. :)");
                        showAlertOnFS(alert, event);
                        switchToPayments(event);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
                        alert.setContentText("Please, enter all required data do add a payment succesfully.");
                        showAlertOnFS(alert, event);
                    }
                }
            }
            else if(patternComma.matcher(addAmountTextField.getText()).matches()
                    || patternComma2.matcher(addAmountTextField.getText()).matches()) {
                if(Double.parseDouble(addAmountTextField.getText().replace(",",".")) <= 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
                    alert.setContentText("The amount shouldn't be equal or less than zero.");
                    showAlertOnFS(alert, event);
                    switchToPayments(event);
                }
                else{
                    amount = Double.parseDouble(addAmountTextField.getText().replace(",","."));
                    payment = addPaymentToDatabase(name, date.toString(), amount);
                    if (payment != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("SUCCESS");
                        alert.setContentText("Payment is added correctly. :)");
                        showAlertOnFS(alert, event);
                        switchToPayments(event);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
                        alert.setContentText("Please, enter all required data do add a payment succesfully.");
                        showAlertOnFS(alert, event);
                    }
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO ADD PAYMENT");
                alert.setContentText("Please, enter amount like currency and keep it between 0.00 and 10000.00");
                showAlertOnFS(alert, event);
            }
        }

    }
    private Payment addPaymentToDatabase(String name, String date, Double amount) {
        payment = null;

        Pattern pattern2 = Pattern.compile("\\d{1,4}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,4}");

        if (pattern2.matcher(String.valueOf(amount)).matches()) {
            String temp = String.valueOf(amount);
            temp = temp + "0";
            amount = Double.parseDouble(temp);
        }
        if (pattern3.matcher(String.valueOf(amount)).matches()) {
            String temp = String.valueOf(amount);
            temp = temp + ".00";
            amount = Double.parseDouble(temp);
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "INSERT INTO payments (name, date, amount) " +
                    "VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, date);
            preparedStatement.setDouble(3, amount);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                payment = new Payment(name, date, String.valueOf(amount));
            }

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (payment != null) {
            System.out.print("Payment added to database...");
        } else {
            System.out.println("Adding payment to database failed...");
        }

        return payment;


    }
    public void removePayment(ActionEvent event) throws IOException {
        if(removeChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO REMOVE PAYMENT");
            alert.setContentText("Choose payment to remove.");
            showAlertOnFS(alert, event);
        }
        else{
            String reading = removeChoiceBox.getValue();
            if (reading == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO REMOVE PAYMENT");
                alert.setContentText("Reading is null.");
                showAlertOnFS(alert, event);
            }
            String[] words = reading.split(" ");
            List<String> lessons1 = List.of(words);
            int last = words.length - 1;
            List<String> nameList = lessons1.subList(0, last - 1);
            String name = "";
            for (int i = 0; i < nameList.size(); i++) {
                name += nameList.get(i) + " ";
            }
            name = name.trim();
            String date = words[last - 1];
            Double amount = Double.parseDouble(words[last]);

            if (name == null || date.isEmpty() || words[last].isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO REMOVE PAYMENT");
                alert.setContentText("All values are null (converting went wrong).");
                showAlertOnFS(alert, event);
            }

            payment = removePaymentFromDatabase(name, date, amount);
            if (payment != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO REMOVE PAYMENT");
                alert.setContentText("RemovePaymentFromDatabase failed.");
                showAlertOnFS(alert, event);
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("SUCCESS");
                alert.setContentText("Payment is removed correctly. :)");
                showAlertOnFS(alert, event);
                switchToPayments(event);
            }
        }

    }
    private Payment removePaymentFromDatabase(String name, String date, Double amount) {
        payment = null;

        int deletedRows = 0;

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "DELETE FROM payments " +
                    "WHERE name LIKE ? AND date LIKE ? AND amount = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, date);
            preparedStatement.setDouble(3, amount);

            deletedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (deletedRows > 0) {
            System.out.print("Payment removed from database...");
        } else {
            System.out.println("Removing payment from database failed...");
            return new Payment("", "", "0.0");
        }

        return payment;


    }
    public void editPayment(ActionEvent event) throws IOException {
        String reading = editChoiceBox.getValue();

        Pattern pattern = Pattern.compile("\\d{1,4}\\.\\d{2}");
        Pattern pattern2 = Pattern.compile("\\d{1,4}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,4}");

        Pattern patternComma = Pattern.compile("\\d{1,4},\\d{2}");
        Pattern patternComma2 = Pattern.compile("\\d{1,4},\\d{1}");

        if(editChoiceBox.getValue() == null && editNameChoiceBox.getValue() == null
                && editAmountTextField.getText().isEmpty() && editDateDatePicker.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
            alert.setContentText("Please, enter all required data do edit a payment succesfully.");
            showAlertOnFS(alert, event);
        }
        else if(editChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
            alert.setContentText("Please, choose payment to edit.");
            showAlertOnFS(alert, event);
        }
        else if(editNameChoiceBox.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
            alert.setContentText("Please, choose a name to edit a payment succesfully.");
            showAlertOnFS(alert, event);
        }
        else if(editDateDatePicker.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
            alert.setContentText("Please, choose date to edit a payment succesfully.");
            showAlertOnFS(alert, event);
        }
        else if(editAmountTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
            alert.setContentText("Please, enter amount to edit a payment succesfully.");
            showAlertOnFS(alert, event);
        }
        else{
            if (reading == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
                alert.setContentText("Please, enter all required data do edit a payment succesfully.");
                showAlertOnFS(alert, event);
            }
            if (pattern.matcher(editAmountTextField.getText()).matches()
                    || pattern2.matcher(editAmountTextField.getText()).matches()
                    || pattern3.matcher(editAmountTextField.getText()).matches()) {
                if(Double.parseDouble(editAmountTextField.getText()) <= 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
                    alert.setContentText("The amount shouldn't be equal or less than zero.");
                    showAlertOnFS(alert, event);
                    switchToPayments(event);
                }
                else{
                    String[] words = reading.split(" ");
                    List<String> lessons1 = List.of(words);
                    int last = words.length - 1;
                    List<String> nameList = lessons1.subList(0, last - 1);
                    String name = "";
                    for (int i = 0; i < nameList.size(); i++) {
                        name += nameList.get(i) + " ";
                    }
                    name = name.trim();
                    String date = words[last - 1];
                    String amount = words[last];
                    String name2 = editNameChoiceBox.getValue();
                    String date2 = editDateDatePicker.getValue().toString();
                    String amount2 = editAmountTextField.getText();

                    if (name == null || date.toString().isEmpty() || amount.isEmpty()
                            || name2 == null || date2.toString().isEmpty() || amount2.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
                        alert.setContentText("Please, enter all required data do edit a payment succesfully.");
                        showAlertOnFS(alert, event);
                    } else {
                        payment = editPaymentInDatabase(name, date, Double.parseDouble(amount), name2, date2, Double.parseDouble(amount2));
                        if (payment == null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("SUCCESS");
                            alert.setContentText("Payment is edited correctly. :)");
                            showAlertOnFS(alert, event);
                            switchToPayments(event);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
                            alert.setContentText("Please, enter all required data do edit a payment succesfully.");
                            showAlertOnFS(alert, event);
                        }
                    }
                }
            }
            else if(patternComma.matcher(editAmountTextField.getText()).matches()
                    || patternComma2.matcher(editAmountTextField.getText()).matches()){
                if(Double.parseDouble(editAmountTextField.getText().replace(",",".")) <= 0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
                    alert.setContentText("The amount shouldn't be equal or less than zero.");
                    showAlertOnFS(alert, event);
                    switchToPayments(event);
                }
                else{
                    String[] words = reading.split(" ");
                    List<String> lessons1 = List.of(words);
                    int last = words.length - 1;
                    List<String> nameList = lessons1.subList(0, last - 1);
                    String name = "";
                    for (int i = 0; i < nameList.size(); i++) {
                        name += nameList.get(i) + " ";
                    }
                    name = name.trim();
                    String date = words[last - 1];
                    String amount = words[last];
                    String name2 = editNameChoiceBox.getValue();
                    String date2 = editDateDatePicker.getValue().toString();
                    String amount2 = editAmountTextField.getText().replace(",",".");

                    if (name == null || date.toString().isEmpty() || amount.isEmpty()
                            || name2 == null || date2.toString().isEmpty() || amount2.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
                        alert.setContentText("Please, enter all required data do edit a payment succesfully.");
                        showAlertOnFS(alert, event);
                    } else {
                        payment = editPaymentInDatabase(name, date, Double.parseDouble(amount), name2, date2, Double.parseDouble(amount2));
                        if (payment == null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("SUCCESS");
                            alert.setContentText("Payment is edited correctly. :)");
                            showAlertOnFS(alert, event);
                            switchToPayments(event);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
                            alert.setContentText("Please, enter all required data do edit a payment succesfully.");
                            showAlertOnFS(alert, event);
                        }
                    }
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FAILED ATTEMPT TO EDIT PAYMENT");
                alert.setContentText("Please, enter amount like currency and keep it between 0.00 and 10000.00");
                showAlertOnFS(alert, event);
            }
        }

    }
    private Payment editPaymentInDatabase(String name, String date, Double amount, String name2, String date2, Double amount2) {
        payment = null;

        int editedRows = 0;
        Pattern pattern2 = Pattern.compile("\\d{1,4}\\.\\d{1}");
        Pattern pattern3 = Pattern.compile("\\d{1,4}");
        if (pattern2.matcher(String.valueOf(amount2)).matches()) {
            String temp = String.valueOf(amount2);
            temp = temp + "0";
            amount2 = Double.parseDouble(temp);
        }
        if (pattern3.matcher(String.valueOf(amount2)).matches()) {
            String temp = String.valueOf(amount2);
            temp = temp + ".00";
            amount2 = Double.parseDouble(temp);
        }

        try {

            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sqlQuery = "UPDATE payments " +
                    "SET name = ?, date = ?, amount = ? " +
                    "WHERE name LIKE ? AND date LIKE ? AND amount = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name2);
            preparedStatement.setString(2, date2);
            preparedStatement.setDouble(3, amount2);
            preparedStatement.setString(4, name);
            preparedStatement.setString(5, date);
            preparedStatement.setDouble(6, amount);

            editedRows = preparedStatement.executeUpdate();

            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (editedRows > 0) {
            System.out.print("Payment edited in database...");
        } else {
            System.out.println("Editing payment in database failed...");
            return new Payment(name2, date2, String.valueOf(amount2));
        }

        return payment;


    }
    public String savingsToDeposit() {
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
