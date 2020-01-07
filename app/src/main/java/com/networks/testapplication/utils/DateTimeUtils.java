package com.networks.testapplication.utils;

import org.threeten.bp.LocalDate;

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

        ZonedDateTime startTime = OffsetDateTime.parse(dateString)
                .atZoneSameInstant(location);


        return startTime.format(outputFormatter);

    }

    public static LocalDate convertToLocalDate(String dateString, String fromFormat) {

        return LocalDate.parse(dateString, org.threeten.bp.format.DateTimeFormatter.ofPattern(fromFormat,Locale.US));

    }

    public static String formatDate(int year, int month, int day){
        String dateString = "" ;
        try {

            String str_date= year+"-"+month+"-"+day;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            Date date = formatter.parse(str_date);
            SimpleDateFormat formatter2 = new SimpleDateFormat("EEEE, MMMM dd",Locale.US);
            dateString = formatter2.format(date.getTime());
        }catch (ParseException e){
            Timber.e(e);
        }
        return dateString;
    }
}
