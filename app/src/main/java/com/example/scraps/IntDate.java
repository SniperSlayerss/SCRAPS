package com.example.scraps;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class IntDate {
    private Integer[] dateArray;
    public Integer Day(){return dateArray[0];}
    public Integer Month(){return dateArray[1];}
    public Integer Year(){return dateArray[2];}

    public IntDate(){
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
        if (Day() >= 10){
            sb.append(Day());
        }
        else{
            sb.append('0');
            sb.append(Day());
        }
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

    public void AddDays(Integer numberOfDays){
        if (numberOfDays >= 0){
            dateArray[0] = Day() + numberOfDays;
            CheckDay();
        }
    }

    private void CheckDay(){
        if (Day() > 28 && Month() == 2 && Year() % 4 != 0){ // if day is greater than 28, month is feb and not a leap year
            dateArray[0] = Day() - 28;
            dateArray[1] = Month() + 1;
            CheckDay();
        }
        if (Day() > 29 && Month() == 2 && Year() % 4 == 0){ // case for leap year
            dateArray[0] = Day() - 29;
            dateArray[1] = Month() + 1;
            CheckDay();
        }
        if (Day() > 30 && (Month() == 4 || Month() == 6 || Month() == 9 || Month() == 11)){ // 30 day months
            dateArray[0] = Day() - 30;
            dateArray[1] = Month() + 1;
            CheckDay();
        }
        if (Day() > 31 && (Month() == 1 || Month() == 3 || Month() == 5 || Month() == 7 || Month() == 8 || Month() == 10 || Month() == 12)){ // 31 day months
            dateArray[0] = Day() - 31;
            dateArray[1] = Month() + 1;
            CheckDay();
        }
        CheckMonth();
    }

    private void CheckMonth(){
        if (Month() > 12){
            dateArray[1] = 1;
            dateArray[2] = Year()+1;
        }
    }

    /**
     * Basically a <= operand since Java doesn't have operator overload functionality.
     * @param firstDate
     * @param secondDate
     * @return True if firstDate <= secondDate, False if firstDate > secondDate
     */
    public static Boolean LessThanEqualTo(IntDate firstDate, IntDate secondDate){
        if (firstDate.Year().equals(secondDate.Year())){
            if (firstDate.Month().equals(secondDate.Month())){
                if (firstDate.Day().intValue() <= secondDate.Day().intValue()){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                if (firstDate.Month().intValue() <= secondDate.Month().intValue()){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        else{
            if (firstDate.Year().intValue() <= secondDate.Year().intValue()){
                return true;
            }
            else{
                return false;
            }
        }
    }

    /**
     * Basically a >= operand since Java doesn't have operator overload functionality.
     * @param firstDate
     * @param secondDate
     * @return True if firstDate >= secondDate, False if firstDate < secondDate
     */
    public static Boolean GreaterThanEqualTo(IntDate firstDate, IntDate secondDate){
        if (firstDate.Year().equals(secondDate.Year())){
            if (firstDate.Month().equals(secondDate.Month())){
                if (firstDate.Day().intValue() >= secondDate.Day().intValue()){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                if (firstDate.Month().intValue() >= secondDate.Month().intValue()){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        else{
            if (firstDate.Year().intValue() >= secondDate.Year().intValue()){
                return true;
            }
            else{
                return false;
            }
        }
    }

    public static IntDate CurrentDate(){
        return new IntDate();
    }
}

