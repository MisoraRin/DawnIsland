package com.yanrou.dawnisland.util;

import android.content.Context;
import android.content.res.Resources;

import com.yanrou.dawnisland.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class ReadableTime {

    private static Resources sResources;

    public static final long SECOND_MILLIS = 1000;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final long WEEK_MILLIS = 7 * DAY_MILLIS;
    public static final long YEAR_MILLIS = 365 * DAY_MILLIS;

    public static final int SIZE = 5;

    public static final long[] MULTIPLES = {
            YEAR_MILLIS,
            DAY_MILLIS,
            HOUR_MILLIS,
            MINUTE_MILLIS,
            SECOND_MILLIS
    };

    public static final int[] UNITS = {
            R.plurals.year,
            R.plurals.day,
            R.plurals.hour,
            R.plurals.minute,
            R.plurals.second
    };

    private static final Calendar sCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
    private static final Object sCalendarLock = new Object();

    private static final SimpleDateFormat DATE_FORMAT_WITHOUT_YEAR =
            new SimpleDateFormat("MM/dd", Locale.getDefault());

    private static final SimpleDateFormat DATE_FORMAT_WIT_YEAR =
            new SimpleDateFormat("yyy/MM/dd", Locale.getDefault());

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yy/MM/dd HH:mm", Locale.getDefault());
    private static final Object sDateFormatLock1 = new Object();

    private static final SimpleDateFormat FILENAMABLE_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault());
    private static final Object sDateFormatLock2 = new Object();

    static {
        // The website use GMT+08:00, so tell user the same
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

        DATE_FORMAT_WITHOUT_YEAR.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
    }

    public static void initialize(Context context) {
        sResources = context.getApplicationContext().getResources();
    }

    static long string2Time(String s) {
        if (s.contains("(")) {
            s = s.substring(0, 10) + " " + s.substring(13);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String getDisplayTime(String time) {
        return getDisplayTime(string2Time(time));
    }


    public static String getDisplayTime(long time) {
        //if (Settings.getPrettyTime()) {
        return getTimeAgo(time);
        //} else {
        //    return getPlainTime(time);
        //}
    }

    public static String getPlainTime(long time) {
        synchronized (sDateFormatLock1) {
            return DATE_FORMAT.format(new Date(time));
        }
    }

    public static String getTimeAgo(long time) {
        Resources resources = sResources;


        long now = System.currentTimeMillis();

        long timeZoneShift = TimeZone.getTimeZone("GMT+08:00").getOffset(now)
                - TimeZone.getDefault().getOffset(now);

        now = System.currentTimeMillis() + timeZoneShift;
        if (time > now + (2 * MINUTE_MILLIS) || time <= 0) {
            return resources.getString(R.string.from_the_future);
        }

        final long diff = now - time;

        if (diff < MINUTE_MILLIS) {
            return resources.getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return resources.getQuantityString(R.plurals.some_minutes_ago, 1, 1);
        } else if (diff < 50 * MINUTE_MILLIS) {
            int minutes = (int) (diff / MINUTE_MILLIS);
            return resources.getQuantityString(R.plurals.some_minutes_ago, minutes, minutes);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return resources.getQuantityString(R.plurals.some_hours_ago, 1, 1);
        } else if (diff < 24 * HOUR_MILLIS) {
            int hours = (int) (diff / HOUR_MILLIS);
            return resources.getQuantityString(R.plurals.some_hours_ago, hours, hours);
        } else if (diff < 48 * HOUR_MILLIS) {
            return resources.getString(R.string.yesterday);
        } else if (diff < WEEK_MILLIS) {
            int days = (int) (diff / DAY_MILLIS);
            return resources.getString(R.string.some_days_ago, days);
        } else {
            synchronized (sCalendarLock) {
                Date nowDate = new Date(now);
                Date timeDate = new Date(time);
                sCalendar.setTime(nowDate);
                int nowYear = sCalendar.get(Calendar.YEAR);
                sCalendar.setTime(timeDate);
                int timeYear = sCalendar.get(Calendar.YEAR);

                if (nowYear == timeYear) {
                    return DATE_FORMAT_WITHOUT_YEAR.format(timeDate);
                } else {
                    return DATE_FORMAT_WIT_YEAR.format(timeDate);
                }
            }
        }
    }

    public static String getTimeInterval(long time) {
        StringBuilder sb = new StringBuilder();
        Resources resources = sResources;

        long leftover = time;
        boolean start = false;

        for (int i = 0; i < SIZE; i++) {
            long multiple = MULTIPLES[i];
            long quotient = leftover / multiple;
            long remainder = leftover % multiple;
            if (start || quotient != 0 || i == SIZE - 1) {
                if (start) {
                    sb.append(" ");
                }
                sb.append(quotient)
                        .append(" ")
                        .append(resources.getQuantityString(UNITS[i], (int) quotient));
                start = true;
            }
            leftover = remainder;
        }

        return sb.toString();
    }

    public static String getFilenamableTime(long time) {
        synchronized (sDateFormatLock2) {
            return FILENAMABLE_DATE_FORMAT.format(new Date(time));
        }
    }
}


