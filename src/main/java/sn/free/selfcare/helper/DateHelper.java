package sn.free.selfcare.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    public static final String FORMAT = "yyMMddHHmmss";
    public static final String SEPARATOR = "_";
    public static final SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);

    public static String getDateTime() {
        return formatter.format(new Date());
    }

    public static String formatWithCurrentDateTime(String value) {
        return value.concat(SEPARATOR).concat(getDateTime());
    }
}
