package com.example.testapkikorki;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;

public class MainPageLogic {

    @FXML
    private TextField filterTextField, hoursPerWeekTextField, weeklyIncomeTextField,
            hoursPerMonthTextField, monthlyIncomeTextField, numberOfStudentsTextField,
            savingsInTotalTextField, paymentsInTotalTextField;
    @FXML
    private Label tableLabel;
    @FXML
    private TableView<Generic> tableView;
    @FXML
    private TableColumn<Generic, String> nameColumn, dateColumn, hoursColumn, timeColumn, typeColumn;


    //all essential information to connect to database
    final String DB_URL = "jdbc:mysql://localhost:3306/myapp1", USERNAME = "root", PASSWORD = "root";
    public BorderPane mainPageBorderPane;
    private Stage stage;
    private Scene scene;
    private FXMLLoader fxmlLoader;

    // coordinates used for moving application across the screen
    private double x = 0, y = 0;



    //method initializing all essential components at the beginning of running main page
    @FXML
    public void initialize() {
        try {
            /*
            initializing table View,
            setting its content and centering it,
            initializing a sorting bar
             */
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
            tableView.setItems(getLessons());
            FilteredList<Generic> filtered = new FilteredList<>(getLessons(), b -> true);
            filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtered.setPredicate(object -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (String.valueOf(object.getTime()).indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else if (object.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else if (object.getDate().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    }
                    else if (object.getTypeOfLesson().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else if (object.getHours().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else {
                        return false;
                    }

                });

            });

            SortedList<Generic> sorted = new SortedList<>(filtered);
            sorted.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sorted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
            connecting to database in order to view statistics of lessons, students etc. in text Fields
        */
        try {
            String sql = "SELECT SUM(hoursWeekly) AS sum FROM students;";
            String sum;
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                if (resultSet.getObject("sum") == null) {
                    sum = "0.0";
                } else {
                    sum = String.valueOf(resultSet.getDouble("sum"));
                }
            } else {
                sum = "0.0";
            }
            Double temp = Double.parseDouble(sum);
            temp = temp * 4;
            String sum3 = String.valueOf(temp);

            hoursPerMonthTextField.setText(sum3);
            hoursPerWeekTextField.setText(sum);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String sql = "SELECT SUM(hoursWeekly * rate) AS sum FROM students;";
            String sum2;
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                if (resultSet.getObject("sum") == null) {
                    sum2 = "0.0";
                } else {
                    sum2 = String.valueOf(resultSet.getDouble("sum"));
                }
            } else {
                sum2 = "0.0";
            }
            Double temp2 = Double.parseDouble(sum2);
            DecimalFormat decimalFormat = new DecimalFormat("0.00 zl");
            temp2 = temp2 * 4;
            String sum4 = decimalFormat.format(temp2);
            sum2 = decimalFormat.format(Double.parseDouble(sum2));

            monthlyIncomeTextField.setText(sum4);
            weeklyIncomeTextField.setText(sum2);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String sql = "SELECT COUNT(*) AS sum FROM students;";
            String sum5;
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                if (resultSet.getObject("sum") == null) {
                    sum5 = "0.0";
                } else {
                    sum5 = String.valueOf(resultSet.getDouble("sum"));
                }
            } else {
                sum5 = "0.0";
            }
            Double temp = Double.parseDouble(sum5);
            Integer temp2 = temp.intValue();
            sum5 = String.valueOf(temp2);

            numberOfStudentsTextField.setText(sum5);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String sql = "SELECT SUM(quantity) AS sum FROM savings;";
            String sum6;
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                if (resultSet.getObject("sum") == null) {
                    sum6 = "0.0";
                } else {
                    sum6 = String.valueOf(resultSet.getDouble("sum"));
                }
            } else {
                sum6 = "0.0";
            }
            DecimalFormat decimalFormat = new DecimalFormat("0.00 zl");
            sum6 = decimalFormat.format(Double.parseDouble(sum6));
            savingsInTotalTextField.setText(sum6);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String sql = "SELECT SUM(amount) AS sum FROM payments;";
            String sum7;
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                if (resultSet.getObject("sum") == null) {
                    sum7 = "0.0";
                } else {
                    sum7 = String.valueOf(resultSet.getDouble("sum"));
                }
            } else {
                sum7 = "0.0";
            }
            DecimalFormat decimalFormat = new DecimalFormat("0.00 zl");
            sum7 = decimalFormat.format(Double.parseDouble(sum7));
            paymentsInTotalTextField.setText(sum7);

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
        Stage stage = (Stage) mainPageBorderPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void movingWhenPressed(MouseEvent event){
        x = event.getSceneX();
        y = event.getSceneY();
    }


    public void centerColumn(TableColumn<Generic, String> nameOfColumn){
        /*
            method used for centering content of columns
         */
        nameOfColumn.setCellFactory(tc -> {
            TextFieldTableCell<Generic, String> cell = new TextFieldTableCell<>();
            cell.setAlignment(javafx.geometry.Pos.CENTER);
            return cell;
        });
    }
    public void setSavingsInTableView() {
        /*
            initializing table View with different content
        */
        tableView.getColumns().clear();
        tableView.getColumns().addAll(nameColumn, dateColumn);

        nameColumn.setText("Quantity");
        dateColumn.setText("Date");

        nameColumn.setMinWidth(300);
        dateColumn.setMinWidth(300);
        nameColumn.setMaxWidth(300);
        dateColumn.setMaxWidth(300);

        nameColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("quantity"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("date"));

        tableView.setItems(getSavings());

        tableLabel.setText("Savings");

        /*
            setting up a working search bar for different tableView
         */
        FilteredList<Generic> filtered = new FilteredList<>(getSavings(), b -> true);
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtered.setPredicate(object -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();


                if (object.getQuantity().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (object.getDate().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }

            });

        });
        SortedList<Generic> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sorted);
    }
    private ObservableList<Generic> getSavings() {
        /*
            retrieving data from database,
            if found - adding it as another object of returned list
            and returning a list of savings
         */
        String sqlQuery = "SELECT * FROM savings;";
        ObservableList<Generic> savingsList = FXCollections.observableArrayList();
        String quantity, date;

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00 zl");
                quantity = resultSet.getString("quantity");
                quantity = String.valueOf(decimalFormat.format(Double.parseDouble(quantity)));
                date = resultSet.getString("date");
                savingsList.add(new Generic(quantity, date));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return savingsList;
    }
    public void setPaymentsInTableView() {
        /*
            initializing table View with different content
        */
        tableView.getColumns().clear();
        tableView.getColumns().addAll(nameColumn, dateColumn, hoursColumn);

        nameColumn.setText("Name");
        dateColumn.setText("Date");
        hoursColumn.setText("Amount");

        nameColumn.setMinWidth(200);
        dateColumn.setMinWidth(200);
        hoursColumn.setMinWidth(200);
        nameColumn.setMaxWidth(200);
        dateColumn.setMaxWidth(200);
        hoursColumn.setMaxWidth(200);

        nameColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("date"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("amount"));

        tableView.setItems(getPayments());

        tableLabel.setText("Payments");

        /*
            setting up a working search bar for different tableView
         */
        FilteredList<Generic> filtered = new FilteredList<>(getPayments(), b -> true);
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtered.setPredicate(object -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (object.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (object.getDate().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (String.valueOf(object.getAmount()).indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }

            });

        });
        SortedList<Generic> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sorted);
    }
    private ObservableList<Generic> getPayments() {
        /*
            retrieving data from database,
            if found - adding it as another object of returned list
            and returning a list of payments
         */
        String sqlQuery = "SELECT * FROM payments;";
        ObservableList<Generic> paymentsList = FXCollections.observableArrayList();
        String name, date, amount;

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00 zl");
                name = resultSet.getString("name");
                date = resultSet.getString("date");
                amount = String.valueOf(decimalFormat.format(Double.parseDouble(resultSet.getString("amount"))));
                paymentsList.add(new Generic(name, date, amount));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return paymentsList;
    }
    public void setStudentsInTableView() {
        /*
            initializing table View with different content
        */
        tableView.getColumns().clear();
        tableView.getColumns().addAll(nameColumn, dateColumn, hoursColumn);

        nameColumn.setText("Name");
        dateColumn.setText("Rate");
        hoursColumn.setText("Hours Weekly");

        nameColumn.setMinWidth(200);
        dateColumn.setMinWidth(200);
        nameColumn.setMaxWidth(200);
        dateColumn.setMaxWidth(200);
        hoursColumn.setMaxWidth(200);
        hoursColumn.setMinWidth(200);

        nameColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("rate"));
        hoursColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Generic, String>, ObservableValue<String>>() {
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

        tableView.setItems(getStudents());

        tableLabel.setText("Students");

        /*
            setting up a working search bar for different tableView
         */
        FilteredList<Generic> filtered = new FilteredList<>(getStudents(), b -> true);
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtered.setPredicate(object -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (object.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (String.valueOf(object.getRate()).indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (String.valueOf(object.getHoursWeekly()).indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }

            });

        });
        SortedList<Generic> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sorted);
    }
    private ObservableList<Generic> getStudents() {
        /*
            retrieving data from database,
            if found - adding it as another object of returned list
            and returning a list of students
         */
        String sqlQuery = "SELECT * FROM students;";
        ObservableList<Generic> studentsList = FXCollections.observableArrayList();
        String name, rate;
        double hoursWeekly;

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00 zl/h");
                name = resultSet.getString("name");
                rate = resultSet.getString("rate");
                rate = String.valueOf(decimalFormat.format(Double.parseDouble(rate)));
                if (resultSet.getString("hoursWeekly") == null || Double.parseDouble(resultSet.getString("hoursWeekly")) == 0.0) {
                    studentsList.add(new Generic(name, rate, 0.0));
                } else {
                    hoursWeekly = Double.parseDouble(resultSet.getString("hoursWeekly"));
                    studentsList.add(new Generic(name, rate, hoursWeekly));
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return studentsList;
    }
    public void setLessonsInTableView() {
        /*
            initializing table View with different content
        */
        if (tableView.getColumns().isEmpty()) {
            tableView.getColumns().addAll(nameColumn, dateColumn, hoursColumn, timeColumn, typeColumn);
        }

        tableView.getColumns().clear();
        tableView.getColumns().addAll(nameColumn, dateColumn, hoursColumn, timeColumn, typeColumn);

        nameColumn.setText("Name");
        dateColumn.setText("Date");
        hoursColumn.setText("Hours");
        timeColumn.setText("Time");
        typeColumn.setText("Type");

        nameColumn.setMinWidth(120);
        dateColumn.setMinWidth(120);
        hoursColumn.setMinWidth(120);
        timeColumn.setMinWidth(120);
        nameColumn.setMaxWidth(120);
        dateColumn.setMaxWidth(120);
        hoursColumn.setMaxWidth(120);
        timeColumn.setMaxWidth(120);
        typeColumn.setMaxWidth(120);
        typeColumn.setMinWidth(120);

        nameColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("date"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("hours"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("time"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<Generic, String>("typeOfLesson"));

        tableView.setItems(getLessons());

        tableLabel.setText("Lessons");

        /*
            setting up a working search bar for different tableView
         */
        FilteredList<Generic> filtered = new FilteredList<>(getLessons(), b -> true);
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtered.setPredicate(object -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(object.getTime()).indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (object.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (object.getTypeOfLesson().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (object.getDate().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (object.getHours().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }

            });
        });
        SortedList<Generic> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sorted);
    }
    private ObservableList<Generic> getLessons() {
        /*
            retrieving data from database,
            if found - adding it as another object of returned list
            and returning a list of students
         */
        String sqlQuery = "SELECT * FROM lessons;";
        ObservableList<Generic> lessonsList = FXCollections.observableArrayList();
        String name, date, hours, time, typeOfLesson;

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                name = resultSet.getString("name");
                date = resultSet.getString("date");
                hours = resultSet.getString("hours");
                time = resultSet.getString("time");
                typeOfLesson = resultSet.getString("typeOfLesson");
                lessonsList.add(new Generic(name, date, hours, time, typeOfLesson));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lessonsList;
    }
    public String savingsToDeposit() {
        /*
            returning data stated above to view as a statistic in textField
         */
        String sql = "SELECT SUM(amount) * 0.75 AS amount FROM payments;";
        double value = 0.0;

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
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
        String sql = "SELECT SUM(quantity) AS quantity FROM savings;";
        Double value = 0.0;

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
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

}
