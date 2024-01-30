package com.example.testapkikorki;

public class Generic {
    private String name;
    public String getName(){return name;}

    private String date;
    public String getDate(){return date;}

    private String rate;
    public String getRate(){return rate;}

    private String hours;
    public String getHours(){return hours;}

    private String quantity;
    public String getQuantity(){return quantity;}

    private String amount;
    public String getAmount(){return amount;}

    private String time;
    public String getTime(){ return time;}

    private Double hoursWeekly;
    public Double getHoursWeekly(){return hoursWeekly;}

    private String typeOfLesson;
    public String getTypeOfLesson(){ return  typeOfLesson;}


    public Generic(String one, String two, String three){
        this.name=one;
        this.date=two;
        this.amount = three;
    }

    public Generic(String one, String two){
        this.quantity = one;
        this.date = two;
        this.name = one;
        this.rate = two;
    }

    public Generic(String one, String two, String three, String four, String typeOfLesson){
        this.name = one;
        this.date= two;
        this.hours = three;
        this.time=four;
        this.typeOfLesson = typeOfLesson;
    }

    public Generic(String one, String two, Double hoursWeekly){
        this.name = one;
        this.rate = two;
        this.hoursWeekly = hoursWeekly;
    }


    public String genericToStringForCalendarNotes(){
        return "NAME: " + this.name + "\tTIME: " + this.time + "\tHOURS: " + this.hours + "\tTYPE OF LESSON: " + this.typeOfLesson + "\n";
    }

}
