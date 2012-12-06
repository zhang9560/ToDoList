package app.todolist.utils;

import java.util.GregorianCalendar;

/**
 * This class is used for simulating class COleDateTime in MFC.
 */
public class JOleDateTime extends GregorianCalendar {

    public JOleDateTime() {
        super();
        init();
    }

    public JOleDateTime(int year, int month, int day) {
        super(year, month, day);
        init();
    }

    public JOleDateTime(int year, int month, int day, int hour, int minute) {
        super(year, month, day, hour, minute);
        init();
    }

    public JOleDateTime(double dateTime) {
        super();
        setDateTime(dateTime);
    }

    public void setDateTime(double dateTime) {
        mDateTime = dateTime;

        long dayOfYear = (long)mDateTime;
        int[] mm = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        int year = 1900;
        dayOfYear--;
        while(true) {
            if(isLeapYear(year)) {
                if (dayOfYear <= 366) {
                    break;
                }else {
                    dayOfYear -= 366;
                    year++;
                }
            } else {
                if (dayOfYear <= 365) {
                    break;
                } else {
                    dayOfYear -= 365;
                    year++;
                }
            }
        }

        if (isLeapYear(year)) {
            mm[FEBRUARY] = 29;
        }

        int month = JANUARY;
        int dayOfMoth = (int)dayOfYear;

        while (dayOfMoth > mm[month]) {
            dayOfMoth -= mm[month++];
        }

        int minutesOfDay = (int)((mDateTime - (long)mDateTime) * 24 * 60);
        int minutesOfHour = minutesOfDay % 60;
        int hoursOfDay = (minutesOfDay - minutesOfHour) / 60;
        set(year, month, dayOfMoth, hoursOfDay, minutesOfHour);
    }

    private void init() {
        double totalDays = 1.0;
        int year = get(YEAR);

        for (int i = 1900; i < year; i++) {
            if (isLeapYear(i))
                totalDays += 366;
            else
                totalDays += 365;
        }
        totalDays += get(DAY_OF_YEAR);

        double minutes = get(HOUR_OF_DAY) * 60 + get(MINUTE);
        mDateTime = totalDays + minutes / (24 * 60);
    }

    public String stringValue() {
        return String.format("%.8f", mDateTime);
    }

    private double mDateTime;
}
