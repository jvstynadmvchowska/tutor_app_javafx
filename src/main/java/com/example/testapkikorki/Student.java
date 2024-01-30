package com.example.testapkikorki;

public class Student extends Generic{

    public String name;
    public String rate;
    public Double hoursWeekly;


    public Student(String name, String rate){
        super(name, rate);
    }
    public Student(String name, String rate, Double hoursWeekly){
        super(name, rate, hoursWeekly);
    }

}
