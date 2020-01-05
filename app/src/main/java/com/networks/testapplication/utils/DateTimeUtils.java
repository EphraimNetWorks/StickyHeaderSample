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
}
