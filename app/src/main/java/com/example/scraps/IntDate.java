package com.example.scraps;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class IntDate {
    private Integer[] dateArray;
    public Integer Day(){return dateArray[0];}
    public Integer Month(){return dateArray[1];}
    public Integer Year(){return dateArray[2];}

    public IntDate(){ // Gets current date as an Integer array of [Day, Month, Year]. Year is the full year (i.e: 2024)
        Date date = new Date();
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
        dateArray = ConvertDate(dateFormatter.format(date));
    }

    public IntDate(String dateAsString){
        dateArray = ConvertDate(dateAsString);
    }

    private Integer[] ConvertDate(String dateAsString){ // Turns a date in format "DD/MM/YYYY" into an Integer array of [Day, Month, Year]. Year is the full year (i.e: 2024).
        char[] dateAsCharArray = dateAsString.toCharArray();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        Integer[] dateAsIntArray = new Integer[3];
        for (char c : dateAsCharArray){
            if (c != '/'){
                sb.append(c);
            }
            else{
                dateAsIntArray[count] = Integer.valueOf(sb.toString());
                sb = new StringBuilder();
                count++;
            }
        }
        dateAsIntArray[count] = Integer.valueOf(sb.toString());
        return dateAsIntArray;
    }

    public String DateAsString(){
        StringBuilder sb = new StringBuilder();
        sb.append(Day());
        sb.append('/');
        if (Month() >= 10){
            sb.append(Month());
        }
        else{
            sb.append('0');
            sb.append(Month());
        }
        sb.append('/');
        sb.append(Year());
        return sb.toString();
    }
}

