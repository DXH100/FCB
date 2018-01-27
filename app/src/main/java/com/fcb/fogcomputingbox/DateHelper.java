
package com.fcb.fogcomputingbox;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateHelper {
    /**
     * 求两个日期差
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 两个日期相差天数
     */
    public static long GetDateMargin(Date beginDate, Date endDate) {
        long margin = 0;

        margin = endDate.getTime() - beginDate.getTime();

        margin = margin / (1000 * 60 * 60 * 24);

        return margin;
    }

    @SuppressWarnings("deprecation")
    private static final Date min =
            new Date(1970, 0, 1, 0, 0, 0);

    @SuppressWarnings("deprecation")
    private static final Date max =
            new Date(8099, 11, 31, 23, 59, 59);

    public static Date getCurrentDateTime() {
        return new Date();
    }

    public static Date getMin() {
        Date date = clone(min);
        return date;
    }

    public static Date getMax() {
        Date date = clone(max);
        return date;
    }

    public static Date getStartOfCurrent() {
        return getStartOf(getCurrentDateTime());
    }

    /**
     * 获取指定日期的起始时间
     * 注: 有效期核心算法, 修改请谨慎.
     */
    @SuppressWarnings("deprecation")
    public static Date getStartOf(final Date date) {
        return new Date(
                date.getYear(),
                date.getMonth(),
                date.getDate());
    }

    @SuppressWarnings("deprecation")
    public static Date clone(final Date date) {
        Date calendar = new Date(1970, 1, 1);
        calendar.setTime(date.getTime());

        return calendar;
    }

    public static final String YYYMMDD = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String toDateTimeString(Date date) {
        Calendar calendar = toCalendar(date);
        return toDateString(calendar, DATE_TIME_PATTERN);
    }

    public static String toDateTimeString(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return toDateString(calendar, DATE_TIME_PATTERN);
    }

    public static Date toDateTimeMillis(long timeMillis) {
        return stringToDate(toDateTimeString(timeMillis));
    }

    public static String strToString(String dateString) {
        return toDateMinuteString(stringToDate(dateString));
    }

    public static final String DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";

    public static String toDateMinuteString(Date date) {
        Calendar calendar = toCalendar(date);
        return toDateString(calendar, DATE_MINUTE_PATTERN);
    }


    public static final String DATE_HOUR_PATTERN = "yyyy-MM-dd HH";

    public static String toDateHourString(Date date) {
        Calendar calendar = toCalendar(date);
        return toDateString(calendar, DATE_HOUR_PATTERN);
    }

    //yyyy-MM-dd HH:mm:ss
    public static String millisToString(long timeMillis) {
        return toDateTimeString(toDateTimeMillis(timeMillis));
    }

    public static String toDateString(Calendar calendar, String pattern) {
        if (calendar == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(calendar.getTime());
    }

    private static final String MONTH_HOUR_MINUTE_PATTERN = "M月d日 HH:mm";

    public static String toMonthHourMinuteString(Date date) {
        Calendar calendar = toCalendar(date);
        return toDateString(calendar, MONTH_HOUR_MINUTE_PATTERN);
    }

    private static final String HOUR_MINUTE_SECOND_PATTREN = "HHmmss";

    public static String getHourMinuteSecondString(Date date) {
        Calendar calendar = toCalendar(date);
        return toDateString(calendar, HOUR_MINUTE_SECOND_PATTREN);
    }

    private static final String HOUR_MINUTE_PATTREN = "HH:mm";

    public static String getHourMinuteString(Date date) {
        Calendar calendar = toCalendar(date);
        return toDateString(calendar, HOUR_MINUTE_PATTREN);
    }

    public static String getHourMinute(String date) {
        return getHourMinuteString(stringToDates(date));
    }

    public static Calendar toCalendar(Date date) {
        if (date == null) {
            return null;
        }

        GregorianCalendar calendar = new GregorianCalendar(1970, 1, 1);
        calendar.setTime(date);

        return calendar;
    }

    /**
     * 获取当前日期和时间
     */
    public static String[] getcurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日#HH:mm");
        Date cuDate = new Date(System.currentTimeMillis());
        String text = formatter.format(cuDate);
        String[] split = text.split("#");
        return split;
    }


    public static String[] getcurrentDates() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy#MM#dd#HH#mm");
        Date cuDate = new Date(System.currentTimeMillis());
        String text = formatter.format(cuDate);
        String[] split = text.split("#");
        return split;
    }

    public static String[] getcurrentDateWithoutYears() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日#HH:mm");
        Date cuDate = new Date(System.currentTimeMillis());
        String text = formatter.format(cuDate);
        String[] split = text.split("#");
        return split;
    }

    public static String getcurrentYear() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date cuDate = new Date(System.currentTimeMillis());
        String text = formatter.format(cuDate);
        return text;
    }

    public static String getyyMM() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
        Date cuDate = new Date(System.currentTimeMillis());
        String text = formatter.format(cuDate);
        return text;
    }

    public static String getyy() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年");
        Date cuDate = new Date(System.currentTimeMillis());
        String text = formatter.format(cuDate);
        return text;
    }

    public static String getCurrentDateToString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date cuDate = new Date(System.currentTimeMillis());
        String text = formatter.format(cuDate);
        return text;
    }


    public static String getMMdd() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        Date cuDate = new Date(System.currentTimeMillis());
        return formatter.format(cuDate);
    }

    public static int getMM(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        Date cuDate = new Date(time);
        return Integer.parseInt(formatter.format(cuDate));
    }

    public static String getmmss(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        Date cuDate = new Date(time);
        return formatter.format(cuDate);
    }

    public static String getHHmm(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date cuDate = new Date(time);
        return formatter.format(cuDate);
    }

    public static String getMMdd(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        Date cuDate = new Date(time);
        return formatter.format(cuDate);
    }

    public static String getYYMM(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
        Date cuDate = new Date(time);
        String format = formatter.format(cuDate);
        return format;
    }

    /**
     * @param index 今天+index天数
     * @return
     */
    public static String getMMdd(int index) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        Date cuDate = new Date(System.currentTimeMillis() + index * 24 * 60 * 60 * 1000);
        return formatter.format(cuDate);
    }

    public static String getYYMMdd(int index) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date cuDate = new Date(System.currentTimeMillis() + index * 24 * 60 * 60 * 1000);
        return formatter.format(cuDate);
    }

    public static String getYYMMdd(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date cuDate = new Date(time);
        return formatter.format(cuDate);
    }

    public static String getMMddString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
        return formatter.format(date);
    }

    /**
     * 将String转date
     */
    public static Date stringToDate(String dateString) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            if (dateString != null) {
                date = format.parse(dateString);
                return date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date stringToDates(String dateString) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            if (dateString != null) {
                date = format.parse(dateString);
                return date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }



    /**
     * 把当前时间转化为毫秒
     */
    public static long getTimeMillis(String dateTime) {
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTimeInMillis();
    }

    /**
     * 设置时间到当前时间间隔多少天
     */
    public static int distanceDay(String date) {
        int day = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = new Date();// 当前时间
            Date d2 = sdf.parse(date);// 传进的时间
            long cha = d2.getTime() - d1.getTime();
            day = new Long(cha / (1000 * 60 * 60 * 24)).intValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }


    public static void main(String[] args) {
        System.out.println(toMonthHourMinuteString(new Date()));
        System.err.println(max.toString());
        Date d = new Date();
        System.out.println(new Date().toString());
        d.setYear(9999 - 1900);
        System.out.println(d.toString());
    }

    /**
     * @param dateStr
     * @param str
     * @return
     */
    public static Date parseStrToDate(String dateStr, String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(str);// 格式化对象
        Date forMatDate = null;
        try {
            forMatDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return forMatDate;
    }


    /**
     * 格式化时间字符串为指定格式
     * @param dateStr
     * @param str
     * @return
     */
//    public static String parseFormatDateStr(String dateStr, String str) {
//        String forMatDate = "";
//        try {
//            Date date = new SimpleDateFormat(str).parse(dateStr);
//            SimpleDateFormat sdf = new SimpleDateFormat(str);// 格式化对象
//            forMatDate = sdf.format(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return forMatDate;
//    }

    /**
     * 格式化时间为指定格式
     *
     * @param dateStr
     * @param str
     * @return
     */
    public static String parseFormatDate(Date dateStr, String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(str);// 格式化对象
        String forMatDate = null;
        try {
            forMatDate = sdf.format(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return forMatDate;
    }

    /**
     * * 计算日期加天数 返回Date
     *
     * @param date
     * @param days
     * @return
     */
    public static Date dayAddDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();// 日历对象
        calendar.setTime(date);// 设置当前日期
        calendar.add(Calendar.DAY_OF_MONTH, days);// 天数+
        return calendar.getTime();// 输出格式化的日期

    }

    /**
     * 计算某时间与当前时间差距的分钟数
     *
     * @param smdate
     * @return
     */
    public static int timesBetween(String smdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(smdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time1 = cal.getTimeInMillis();

        long time2 = new Date().getTime();
        long between_times = (time2 - time1) / (1000 * 60);
        return Integer.parseInt(String.valueOf(between_times));
    }


    /**
     * 计算日期间隔天数
     *
     * @param smdate
     * @param bdate
     * @return
     */
    public static int daysBetween(String smdate, String bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(smdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time1 = cal.getTimeInMillis();
        try {
            cal.setTime(sdf.parse(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 计算日期间隔天数
     *
     * @param smdate
     * @param bdate
     * @return
     */
    public static int daysBetweenBySecond(String smdate, String bdate) {
        long seconds = calBetweenSeconds(smdate, bdate);
        long between_days = seconds / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 计算时间差，毫秒数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long calBetweenSeconds(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time1 = cal.getTimeInMillis();

        try {
            cal.setTime(sdf.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time2 = cal.getTimeInMillis();
        return time2 - time1;
    }


    /**
     * 计算日期加天数 返回String
     *
     * @param sDate
     * @param days
     * @return
     */
    public static String dayAdd(String sDate, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化对象
        Calendar calendar = Calendar.getInstance();// 日历对象
        try {
            calendar.setTime(sdf.parse(sDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }// 设置当前日期
        calendar.add(Calendar.DAY_OF_MONTH, days);// 天数+
        return sdf.format(calendar.getTime());// 输出格式化的日期
    }

    // 获取今天的日期
    public static String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    // 获取前几天的日期
    public static String getFrontDate(String curdate, int interval) {
        Date date = null;
        String frontDate = null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = sf.parse(curdate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -interval);
            date = calendar.getTime();
            frontDate = sf.format(date.getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return frontDate;
    }

    /**
     * 格式化DateTime类型时间去掉毫秒数
     *
     * @param formatdate
     * @return
     */
    public static String forFormatDate(String formatdate) {

        Date date = null;
        String blan = null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            date = sf.parse(formatdate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            date = calendar.getTime();
            blan = sf.format(date.getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return blan;
    }


    /**
     * 格式化DateTime类型时间去掉毫秒数返回时间戳至分钟
     *
     * @param formatdate
     * @return
     */
    public static String forFormatDateStr(String formatdate) {

        Date date = null;
        String blan = null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            date = sf.parse(formatdate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            date = calendar.getTime();
            blan = sf.format(date.getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return blan;
    }

    /**
     * 将时间字符串转换为时间戳
     *
     * @param user_time
     * @return
     */
    public static String getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(user_time);
            re_time = String.valueOf(d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    /**
     * 格式化DateTime类型时间去掉毫秒数返回时间戳年月日
     *
     * @param formatdate
     * @return
     */
    public static String forFormatDateStrToDay(String formatdate) {

        Date date = null;
        String blan = null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = sf.parse(formatdate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            date = calendar.getTime();
            blan = sf.format(date.getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return blan;
    }


    // 获取今天的日期
    public static String getTodayDateBySecond() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
    }

    // 获取指定的时间戳
    public static String getFixStamp(int day, int hour, int seccond) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, day);
        cal.add(Calendar.HOUR, hour);
        cal.add(Calendar.SECOND, seccond);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
    }

    /**
     * 获取几个月前或几个月后的日期 日期格式: 2016-8-8
     *
     * @param date
     * @param interval 正值为加,负值为日期倒退
     * @return
     */
    public static String intervalDate(String date, int interval) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = null;
        try {
            dt = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.MONTH, interval); //日期加减 interval 个月
        Date dt1 = rightNow.getTime();
        String threeMonthAgo = sdf.format(dt1);
        return threeMonthAgo;
    }

    //判断是否是符合规律的时间格式 : 2016-01-01
    public static boolean isValidDate(String str) {
        //String str = "2007-01-02";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = (Date) formatter.parse(str);
            return str.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    public static String forFormatDateYMD(String formatdate) {

        Date date = null;
        String blan = null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = sf.parse(formatdate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            date = calendar.getTime();
            blan = sf.format(date.getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return blan;
    }

    /**
     * 获取下一天的时间
     */
    public static String getTomorrowDate() {
        String tomorrow = "";
        try {
            //获取当前日期
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = sf.format(date);

            //通过日历获取下一天日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(sf.parse(nowDate));
            cal.add(Calendar.DAY_OF_YEAR, +1);
            tomorrow = sf.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tomorrow;
    }

    /**
     * 10位时间戳转Date yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static String timestampToString(Integer time) {
        //int转long时，先进行转型再进行计算，否则会是计算结束后在转型
        long temp = (long) time * 1000;
        Timestamp ts = new Timestamp(temp);
        String tsStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //方法一
            tsStr = dateFormat.format(ts);
            LogUtils.e(tsStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tsStr;
    }

    /**
     * 时间戳转换日期
     *
     * @param time
     * @return
     */
//    public static String exchangeAt2date(long time) {
//        //int转long时，先进行转型再进行计算，否则会是计算结束后在转型
//        Date curr = new Date(System.currentTimeMillis());
//        Date yes = new Date(System.currentTimeMillis() - (60 * 60 * 24 * 1000));
//        Date date = new Date(time);
//        if (getYYMMdd(date.getTime()).equals(getYYMMdd(curr.getTime()))) {
//            return UIUtils.getString(R.string.today);
//        }
//        if (getYYMMdd(date.getTime()).equals(getYYMMdd(yes.getTime()))) {
//            return UIUtils.getString(R.string.yesterday);
//        }
//        return getWeek(date);
//
//    }
//    //2017-03-24  2017-03-23


    /**
     * 小于当前时间
     *
     * @param date
     * @return
     */
    public static boolean lessDate(Date date) {
        Date day = new Date();
        boolean flag = false;
        if (date != null) {
            flag = date.before(day);
        }
        return flag;
    }

    /**
     * 大于当前时间
     *
     * @param date
     * @return
     */
    public static boolean greaterDate(Date date) {
        Date day = new Date();
        boolean flag = true;
        if (date != null) {
            flag = date.after(day);
        }
        return flag;
    }

    /**
     * 生成日期
     *
     * @return
     */
    public static String generateWord() {
        String date = DateHelper.parseFormatDate(new Date(), "yyyyMMdd");
        String path = date;
        return path;
    }

    /**
     * 获取2017年到今年的月份集合
     *
     * @return
//     */
//    public static ArrayList<String> getyyMMList() {
//        String[] strings = getcurrentDates();
//        int year = Integer.parseInt(strings[0]);
//        int month = Integer.parseInt(strings[1]);
//
//        ArrayList<String> list = new ArrayList<>();
//        while (true) {
//            if (year < 2017) break;
//            if (month > 0) {
//                if (month < 10) {
//                    list.add(year + UIUtils.getString(R.string.year) + "0" + month + UIUtils.getString(R.string.month));
//                } else {
//                    list.add(year + UIUtils.getString(R.string.year) + month + UIUtils.getString(R.string.month));
//                }
//
//                month--;
//            } else {
//                month = 12;
//                year--;
//            }
//        }
//        return list;
//    }

    /**
     * @return
     * @auther denghx
     * @date 2017/4/7 13:59
     * 获取2017年到今年的年份集合
//     */
//    public static ArrayList<String> getMMList() {
//        String[] strings = getcurrentDates();
//        int year = Integer.parseInt(strings[0]);
//        ArrayList<String> list = new ArrayList<>();
//        while (true) {
//            if (year < 2017) break;
//            list.add(year + UIUtils.getString(R.string.year));
//            year--;
//        }
//        return list;
//    }
}