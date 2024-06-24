package net.bc100dev.osintgram4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        return sdf.format(new Date(record.getMillis())) +
                " [" + record.getLevel() + "]" +
                "(" + record.getSourceClassName() + "#" + record.getSourceMethodName() + "): " +
                formatMessage(record) +
                System.lineSeparator();
    }

}
