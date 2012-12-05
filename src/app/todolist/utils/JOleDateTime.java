package app.todolist.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class is used for simulating class COleDateTime in MFC.
 */
public class JOleDateTime {

    public JOleDateTime(GregorianCalendar cal) {
        // Start from 1899-12-30.
        double totalDays = 1.0;
        int year = cal.get(Calendar.YEAR);

        for(int i = 1900; i < year; i++) {
            if(cal.isLeapYear(i))
                totalDays += 366;
            else
                totalDays += 365;
        }
        totalDays += cal.get(Calendar.DAY_OF_YEAR);

        double minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        mDateTime = totalDays + minutes / (24 * 60);
    }

    public JOleDateTime(double dateTime) {
        mDateTime = dateTime;
    }

    public void setDateTime(double dateTime) {
        mDateTime = dateTime;
    }

    public double getDateTime() {
        return mDateTime;
    }

    public String stringValue() {
        return String.format("%.8f", mDateTime);
    }

    private double mDateTime;
}
