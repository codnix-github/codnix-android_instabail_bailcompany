package com.bailcompany.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

// TODO: Auto-generated Javadoc

/**
 * The Class Commons is a utility class which provide the basic functions used
 * in project.
 */
public class Commons {

    /**
     * Checks if is empty.
     *
     * @param str the str
     * @return true, if is empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().equalsIgnoreCase("null")
                || str.trim().length() == 0;
    }

    public static boolean isEmpty(ArrayList<String> str) {
        return str == null || str.size() == 0;
    }

    /**
     * Gets the date time.
     *
     * @return the date time
     */
    public static String getDateTime() {

        Date d = new Date(Calendar.getInstance().getTimeInMillis());
        String format = "yyyy-MM-dd kk:mm:ss";
        return new SimpleDateFormat(format).format(d);
    }

    /**
     * Mills to date.
     *
     * @param mills the mills
     * @return the string
     */
    public static String millsToDate(long mills) {

        Date d = new Date(mills);
        String format = "yyyy-MM-dd";
        return new SimpleDateFormat(format).format(d);
    }

    /**
     * String to calander.
     *
     * @param date the date
     * @param cal  the cal
     */
    public static void stringToCalander(String date, Calendar cal) {

        String[] str = date.split("-");
        if (str.length == 3)
            cal.set(Integer.parseInt(str[0]), Integer.parseInt(str[1]) - 1,
                    Integer.parseInt(str[2]));
    }

    /**
     * Date time to millis.
     *
     * @param datetime the datetime
     * @return the long
     */
    public static long dateTimeToMillis(String datetime) {

        Calendar cal = Calendar.getInstance();
        try {
            String[] arr = datetime.split(" ");
            String[] str = arr[0].split("-");
            if (str.length == 3)
                cal.set(Integer.parseInt(str[0]), Integer.parseInt(str[1]) - 1,
                        Integer.parseInt(str[2]));
            if (arr[1].contains(":")) {
                str = arr[1].split(":");
                if (str.length == 3) {
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(str[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(str[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal.getTimeInMillis();
    }

    public static long dateTimeToMillis2(String datetime) {

        Calendar cal = Calendar.getInstance();
        try {
            String[] arr = datetime.split(" ");
            String[] str = arr[0].split("-");
            if (str.length == 3)
                cal.set(Integer.parseInt(str[2]), Integer.parseInt(str[0]) - 1,
                        Integer.parseInt(str[1]));
            if (arr[1].contains(":")) {
                str = arr[1].split(":");
                if (str.length == 3) {
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(str[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(str[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal.getTimeInMillis();
    }

    /**
     * Date time to millis1.
     *
     * @param datetime the datetime
     * @return the long
     */
    public static long dateTimeToMillis1(String datetime) {

        Calendar cal = Calendar.getInstance();
        try {
            String[] arr = datetime.split(" ");
            String[] str = arr[0].split("-");
            if (str.length == 3)
                cal.set(Integer.parseInt(str[2]), Integer.parseInt(str[0]) - 1,
                        Integer.parseInt(str[1]));
            str = arr[1].split(":");
            if (str.length == 3) {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(str[1]));
                cal.set(Calendar.SECOND, Integer.parseInt(str[2]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal.getTimeInMillis();
    }

    /**
     * Mills to date time.
     *
     * @param mills the mills
     * @return the string
     */
    public static String millsToDateTime(long mills) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mills);

        int d = cal.get(Calendar.DAY_OF_MONTH);
        int m = cal.get(Calendar.MONTH) + 1;
        int y = cal.get(Calendar.YEAR);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        String date = y + "-" + (m < 10 ? "0" + m : m) + "-"
                + (d < 10 ? "0" + d : d) + " " + (h < 10 ? "0" + h : h) + ":"
                + (min < 10 ? "0" + min : min);
        return date;
    }

    public static String extractDate(String date) {

        if (!isEmpty(date) && date.contains("(") && date.contains(")")) {
            String millis = date.substring(date.indexOf("(") + 1,
                    date.indexOf(")"));
            Log.d("millis", millis);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(millis));
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            return sdf.format(cal.getTime());
        }

        return "" + date;
    }

    /**
     * Str to double.
     *
     * @param str the str
     * @return the double
     */
    public static double strToDouble(String str) {

        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Str to int.
     *
     * @param str the str
     * @return the int
     */
    public static int strToInt(String str) {

        try {
            return (int) strToDouble(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Str to long.
     *
     * @param str the str
     * @return the long
     */
    public static long strToLong(String str) {

        return (long) strToDouble(str);
    }

    /**
     * Checks if is before today.
     *
     * @param date the date
     * @return true, if is before today
     */
    public static boolean isBeforeToday(String date) {

        Calendar today = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        stringToCalander(date, cal);

        return cal.compareTo(today) == -1;
    }

    /**
     * Checks if is between today.
     *
     * @param start the start
     * @param end   the end
     * @return true, if is between today
     */
    public static boolean isBetweenToday(String start, String end) {

        try {
            Calendar today = Calendar.getInstance();
            Calendar calStart = Calendar.getInstance();
            stringToCalander(start, calStart);

            Calendar calEnd = Calendar.getInstance();
            stringToCalander(end, calEnd);

            return calStart.compareTo(today) <= 0
                    && calEnd.compareTo(today) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static long jsonDateToTimeMillis(String str) {
        try {
            str = str.substring(str.indexOf("(") + 1, str.indexOf(")"));

            Calendar cal = Calendar.getInstance();
            if (str.contains("+")) {
                int i = str.indexOf("+");
                cal.setTimeInMillis(Long.parseLong(str.substring(0, i)));
                cal.setTimeZone(TimeZone.getTimeZone("GMT+"
                        + str.substring(i + 1)));
            } else if (str.contains("-")) {
                int i = str.indexOf("-");
                cal.setTimeInMillis(Long.parseLong(str.substring(0, i)));
                cal.setTimeZone(TimeZone.getTimeZone("GMT-"
                        + str.substring(i + 1)));
            } else
                cal.setTimeInMillis(Long.parseLong(str));
            return cal.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String timeMillisToJsonDate(long time, boolean exlcudeTimezone) {
        if (exlcudeTimezone)
            return "/Date(" + time + ")/";

        int z = Calendar.getInstance().getTimeZone().getRawOffset()
                / (60 * 1000);
        boolean neg = z < 0;
        if (neg)
            z = z * -1;
        int h = z / 60;
        int m = z - (h * 60);

        String tz = String.format(Locale.US, "%s%02d%02d", neg ? "-" : "+", h,
                m);
        return "/Date(" + time + tz + ")/";

    }

    public static String changeDateFormat(String s) {
        Date d = null;
        String date = "";
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            d = new Date();
            d = formatter.parse(s);
            date = new SimpleDateFormat("MM/dd/yyyy").format(d);

        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return date;
    }


}
