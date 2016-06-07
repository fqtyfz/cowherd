package io.github.notsyncing.cowherd.utils;

import java.io.*;
import java.net.HttpCookie;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StringUtils
{
    private static final DateFormat cookiesDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");

    static {
        cookiesDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String appendUrl(String source, String append)
    {
        return source.endsWith("/") ? source + append : source + "/" + append;
    }

    public static boolean isEmpty(String s)
    {
        return (s == null) || (s.isEmpty());
    }

    public static String exceptionStackToString(Throwable ex)
    {
        try (StringWriter writer = new StringWriter(); PrintWriter w = new PrintWriter(writer)) {
            ex.printStackTrace(w);
            return writer.toString();
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }

        return null;
    }

    public static String streamToString(InputStream s) throws IOException
    {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = s.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }

    public static Date parseHttpDateString(String s) throws ParseException
    {
        return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).parse(s);
    }

    public static String dateToHttpDateString(Date d)
    {
        DateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        f.setTimeZone(TimeZone.getTimeZone("GMT"));
        return f.format(d);
    }

    public static String cookieToString(HttpCookie cookie)
    {
        StringBuilder b = new StringBuilder();
        b.append(cookie.getName()).append("=").append(cookie.getValue());

        if (cookie.getPath() != null) {
            b.append("; path=").append(cookie.getPath());
        }

        if (cookie.getDomain() != null) {
            b.append("; domain=").append(cookie.getDomain());
        }

        if (cookie.getPortlist() != null) {
            b.append("; port=").append(cookie.getPortlist());
        }

        if (cookie.getMaxAge() != -1) {
            b.append("; max-age=").append(cookie.getMaxAge());

            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            c.add(Calendar.SECOND, (int)cookie.getMaxAge());
            b.append("; expires=").append(cookiesDateFormat.format(c.getTime()));
        }

        if (cookie.getSecure()) {
            b.append("; secure");
        }

        if (cookie.isHttpOnly()) {
            b.append("; httponly");
        }

        return b.toString();
    }
}
