package com.example.testapkikorki;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;

public class CalendarLogic {

    // tiles representing days in calendar
    @FXML
    private Label t1, t2, t3, t4, t5, t6, t7, t8, t9, t10,
            t11, t12, t13, t14, t15, t16, t17, t18, t19,
            t20, t21, t22, t23, t24, t25, t26, t27, t28,
            t29, t30, t31, t32, t33, t34, t35, t36, t37,
            t38, t39, t40, t41, t42;

    @FXML
    private ChoiceBox<String> yearChoiceBox, monthChoiceBox;


    private Stage stage;
    private Scene scene;
    private FXMLLoader fxmlLoader;

    //all essential information to connect to database
    final String DB_URL = "jdbc:mysql://localhost:3306/myapp1", USERNAME = "root", PASSWORD = "root";

    // lists of breaks (holidays) in specified years
    final public List<String> breaks2023 = Arrays.asList(
            "2023-01-01", "2023-01-06", "2023-04-09", "2023-04-10", "2023-05-01", "2023-05-03", "2023-05-28", "2023-06-08", "2023-08-15", "2023-11-01", "2023-11-11", "2023-12-25", "2023-12-26");
    final public List<String> breaks2024 = Arrays.asList(
            "2024-01-01", "2024-01-06", "2024-03-31", "2024-04-01", "2024-05-01", "2024-05-03", "2024-05-19", "2024-05-30", "2024-08-15", "2024-11-01", "2024-11-11", "2024-12-25", "2024-12-26");
    final public List<String> breaks2025 = Arrays.asList(
            "2025-01-01", "2025-01-06", "2025-04-20", "2025-04-21", "2025-05-01", "2025-05-03", "2025-06-08", "2025-06-19", "2025-08-15", "2025-11-01", "2025-11-11", "2025-12-25", "2025-12-26");
    final public List<String> breaks2026 = Arrays.asList(
            "2026-01-01", "2026-01-06", "2026-04-05", "2026-04-06", "2026-05-01", "2026-05-03", "2026-05-24", "2026-06-04", "2026-08-15", "2026-11-01", "2026-11-11", "2026-12-25", "2026-12-26");
    final public List<String> breaks2027 = Arrays.asList(
            "2027-01-01", "2027-01-06", "2027-03-28", "2027-03-29", "2027-05-01", "2027-05-03", "2027-05-16", "2027-05-27", "2027-08-15", "2027-11-01", "2027-11-11", "2027-12-25", "2027-12-26");
    public BorderPane calendarBorderPane;

    // coordinates used for moving application across the screen
    private double x = 0, y = 0;



    //method initializing all essential components at the beginning of running lessons page
    @FXML
    private void initialize(){
        yearChoiceBox.setItems(getYearList());
        monthChoiceBox.setItems(getMonthList());

        LocalDate currentDate = LocalDate.now();

        List<String> years = Arrays.asList("2023", "2024", "2025", "2026" ,"2027");

        int currentMonthInd = Calendar.getInstance().getTime().getMonth();
        int currentYearInd = years.indexOf(String.valueOf(currentDate.getYear()));

        yearChoiceBox.setValue(getYearList().get(currentYearInd));
        monthChoiceBox.setValue(getMonthList().get(currentMonthInd));

        renderCalendar(yearChoiceBox,monthChoiceBox);

        yearChoiceBox.setOnAction(event -> {
            renderCalendar(yearChoiceBox,monthChoiceBox);

        });

        monthChoiceBox.setOnAction(event -> {
            renderCalendar(yearChoiceBox,monthChoiceBox);

        });

    }
    @FXML
    public void minimizeWindow (MouseEvent e){
        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
        s.setIconified(true);
    }
    @FXML
    public void maximiseWindow (MouseEvent e){
        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
        s.setFullScreenExitHint("");
        if(s.isFullScreen()){
            s.setFullScreen(false);
        }
        else{
            s.setFullScreen(true);
        }

    }

    @FXML
    public void closeWindow (MouseEvent e){
        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
        s.close();
    }

    /*
        methods movingWhenDragged and movingWhenPressed are used for moving the application across the screen
        when user grabs and moves it around with mouse click
     */
    @FXML
    public void movingWhenDragged(MouseEvent event){
        Stage stage = (Stage) calendarBorderPane.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void movingWhenPressed(MouseEvent event){
        x = event.getSceneX();
        y = event.getSceneY();
    }
    @FXML
    public void nextMonthSwitch (MouseEvent event) throws IOException {
        int maxIndMonths = getMonthList().size() - 1;
        String currentMonth = monthChoiceBox.getValue();
        int currentMonthInd = getMonthList().indexOf(currentMonth);

        int maxIndYear = getYearList().size() - 1;
        String currentYear = yearChoiceBox.getValue();
        int currentYearInd = getYearList().indexOf(currentYear);

        if(currentMonthInd == maxIndMonths){
            currentMonthInd = 0;
            currentYearInd ++;
        }
        else{
            currentMonthInd ++;
        }
        if(currentYearInd > maxIndYear){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CANNOT GO FURTHER");
            alert.setContentText("Calendar is planned until the end of 2027.");
            showAlertOnFS(alert, event);
        }
        else{
            currentMonth = getMonthList().get(currentMonthInd);
            currentYear = getYearList().get(currentYearInd);
            yearChoiceBox.setValue(currentYear);
            monthChoiceBox.setValue(currentMonth);
            renderCalendar(yearChoiceBox, monthChoiceBox);
        }

    }
    @FXML
    public void previousMonthSwitch (MouseEvent event) throws IOException {
        int maxIndMonths = getMonthList().size() - 1;
        String currentMonth = monthChoiceBox.getValue();
        int currentMonthInd = getMonthList().indexOf(currentMonth);

        String currentYear = yearChoiceBox.getValue();
        int currentYearInd = getYearList().indexOf(currentYear);

        if(currentMonthInd == 0){
            currentMonthInd = maxIndMonths;
            currentYearInd --;
        }
        else{
            currentMonthInd --;
        }
        if(currentYearInd < 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CANNOT GO EARLIER");
            alert.setContentText("Calendar is planned from 2023.");
            showAlertOnFS(alert, event);
        }
        else{
            currentMonth = getMonthList().get(currentMonthInd);
            currentYear = getYearList().get(currentYearInd);
            yearChoiceBox.setValue(currentYear);
            monthChoiceBox.setValue(currentMonth);
            renderCalendar(yearChoiceBox, monthChoiceBox);
        }
    }


    public void renderCalendar(ChoiceBox<String> year, ChoiceBox<String> month){

        List<String> monthsList = Arrays.asList("error", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November" , "December");
        int monthInd = monthsList.indexOf(month.getValue());

        ObservableList<Label> calendarTiles = FXCollections.observableArrayList(t1, t2, t3, t4,
                t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19,
                t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32, t33, t34, t35,
                t36, t37, t38, t39, t40, t41, t42);

        Integer[][] calendarMatrix = returnCalendarAsMatrix(Integer.parseInt(year.getValue()),monthInd);

        int calendarTileInd = 0;
        for(int i = 0; i < calendarMatrix.length; i++){
            for(int j = 0; j < calendarMatrix[0].length; j++){
                String day = calendarMatrix[i][j].toString();
                if(day.equals("0")){
                    calendarTiles.get(calendarTileInd).setText("");
                }
                else{
                    calendarTiles.get(calendarTileInd).setText(day);
                }
                calendarTileInd++;

            }

        }

        List<Integer> weekends = Arrays.asList(5, 6, 12, 13, 19, 20, 26, 27, 33, 34, 40, 41);

        try {
            String sqlQuery = "SELECT * FROM lessons;";
            List<Lesson> lessonsList = new java.util.ArrayList<>(List.of());
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                lessonsList.add(new Lesson(resultSet.getString("name"), resultSet.getString("date"),resultSet.getString("hours"), resultSet.getString("time"), resultSet.getString("typeOfLesson")));
            }

            for(int i = 0; i < calendarTiles.size(); i++){
                String dateString = "";
                if(calendarTiles.get(i).getText().length() < 2){
                    if(monthsList.indexOf(month.getValue()) < 10){
                        dateString = year.getValue() + "-0" + monthsList.indexOf(month.getValue()) + "-0" + calendarTiles.get(i).getText();
                    }
                    else{
                        dateString = year.getValue() + "-" + monthsList.indexOf(month.getValue()) + "-0" + calendarTiles.get(i).getText();
                    }

                }
                else{
                    if(monthsList.indexOf(month.getValue()) < 10){
                        dateString = year.getValue() + "-0" + monthsList.indexOf(month.getValue()) + "-" + calendarTiles.get(i).getText();
                    }
                    else{
                        dateString = year.getValue() + "-" + monthsList.indexOf(month.getValue()) + "-" + calendarTiles.get(i).getText();
                    }
                }
                if((year.getValue().equals("2023") && breaks2023.contains(dateString))
                        || (year.getValue().equals("2024") && breaks2024.contains(dateString))
                        || (year.getValue().equals("2025") && breaks2025.contains(dateString))
                        || (year.getValue().equals("2026") && breaks2026.contains(dateString))
                        || (year.getValue().equals("2027") && breaks2027.contains(dateString))){
                    calendarTiles.get(i).setStyle("-fx-background-color: #dd99ff;");
                    Tooltip holidaysTooltip = new Tooltip("HOLIDAY");
                    holidaysTooltip.setStyle("-fx-font-size: 14; -fx-background-color: #dd99ff; -fx-background-radius: 0; -fx-text-fill: black;");
                    holidaysTooltip.setShowDelay(Duration.millis(150));
                    Tooltip.install(calendarTiles.get(i), holidaysTooltip);
                }
                else{
                    if(weekends.contains(i)){
                        calendarTiles.get(i).setStyle("-fx-background-color: #efefef;");
                    }
                    else{
                        calendarTiles.get(i).setStyle("-fx-background-color: white;");
                    }
                }
                String textForToolTip = "";
                boolean isToolTipNeeded = false;
                for(int j = 0; j < lessonsList.size(); j++){
                    if(lessonsList.get(j).getDate().equals(dateString)){
                        textForToolTip += lessonsList.get(j).genericToStringForCalendarNotes();
                        calendarTiles.get(i).setStyle("-fx-background-color: #ffd147;");
                        isToolTipNeeded = true;
                    }
                }
                if(isToolTipNeeded){
                    Tooltip lessonsTooltip = new Tooltip(textForToolTip);
                    lessonsTooltip.setStyle("-fx-font-size: 14; -fx-background-color: #ffd147; -fx-background-radius: 0; -fx-text-fill: black;");
                    lessonsTooltip.setShowDelay(Duration.millis(150));
                    Tooltip.install(calendarTiles.get(i), lessonsTooltip);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static Integer[][] returnCalendarAsMatrix(int yearAsNumber, int monthAsNumber) {
        int day = 1;
        LocalDate dateToWorkWith = LocalDate.of(yearAsNumber, monthAsNumber, day);
        Integer[][] calendarMatrixToReturn = new Integer[6][7];
        int howManyDays = dateToWorkWith.lengthOfMonth();
        Calendar currentDate = Calendar.getInstance();


        while (day <= howManyDays) {
            dateToWorkWith = LocalDate.of(yearAsNumber, monthAsNumber, day);
            DayOfWeek dayOfTheWeek = dateToWorkWith.getDayOfWeek();
            int dayOfTheWeekInd = dayOfTheWeek.getValue() - 1;
            currentDate.setTime(Date.from(dateToWorkWith.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            int weekInd = currentDate.get(Calendar.WEEK_OF_MONTH);
            calendarMatrixToReturn[weekInd][dayOfTheWeekInd] = day;
            day++;
        }
        for(int i = 0; i < calendarMatrixToReturn.length; i++){
            for(int j = 0; j < calendarMatrixToReturn[0].length; j++){
                if(calendarMatrixToReturn[i][j] == null){
                    calendarMatrixToReturn[i][j] = 0;
                }
            }
        }
        return calendarMatrixToReturn;
    }

    // methods getYearList and getMonthList are for providing list of objects to appear in choice boxes
    public ObservableList<String> getYearList(){
        ObservableList<String> yearList = FXCollections.observableArrayList();
        yearList.clear();
        String [] yearsArray = {"2023", "2024", "2025", "2026" ,"2027"};
        for (int i = 0 ; i< yearsArray.length; i++){
            yearList.add(yearsArray[i]);
        }
        return yearList;
    }
    public ObservableList<String> getMonthList(){
        ObservableList<String> monthsList = FXCollections.observableArrayList();
        String [] monthsArray = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November" , "December"};
        for (int i = 0 ; i< monthsArray.length; i++){
            monthsList.add(monthsArray[i]);
        }
        return monthsList;
    }
    public String savingsToDeposit(){
        /*
            returning data stated above to view as a statistic in textField
         */
        String sqlQuery = "SELECT SUM(amount) * 0.75 AS amount FROM payments;";
        Double value = 0.0;

        try{
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                value = (resultSet.getDouble("amount"));
            }


        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        Double result = (value - Double.parseDouble(savedSavings()));
        return result.toString();
    }
    public String savedSavings(){
        /*
            returning data stated above to view as a statistic in textField
         */
        String sqlQuery = "SELECT SUM(quantity) AS quantity FROM savings;";
        Double value = 0.0;

        try{
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                value = (resultSet.getDouble("quantity"));
            }


        } catch(SQLException e){
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
