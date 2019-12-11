package com.networks.testapplication.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class DateTimeUtils {



    public static String convertDate(String dateString, String fromFormat, String toFormat) {

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(toFormat, Locale.US);

        ZoneId location = ZoneId.of(AppPreferencesHelper.getInstance().getDefaultTimeZone());
        ZoneId deviceLocation = ZoneId.of(TimeZone.getDefault().getID());
        TimeZone tz = TimeZone.getTimeZone(AppPreferencesHelper.getInstance().getDefaultTimeZone());
        ZonedDateTime startTime = OffsetDateTime.parse(dateString)
                .atZoneSameInstant(location);
        String startDate = startTime.format(outputFormatter);
        Date date = null;
        android.icu.text.SimpleDateFormat format = new android.icu.text.SimpleDateFormat(fromFormat,Locale.US);
        try {
            date = format.parse(dateString);
            System.out.println(date);
        } catch (ParseException e) {
            Timber.e(e, "getEventTime() exception log");
        }
        if( !location.equals(deviceLocation)) {
            Timber.d("final date is zzz: " + location + deviceLocation);
            String locationName = tz.getDisplayName(tz.inDaylightTime(date), TimeZone.SHORT);

            return startDate + " " + locationName;

        } else {
            return startDate;

        }

    }
}
