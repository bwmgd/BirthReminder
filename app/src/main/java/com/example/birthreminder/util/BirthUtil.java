package com.example.birthreminder.util;

import android.graphics.Color;
import cn.carbs.android.gregorianlunarcalendar.library.util.Util;
import com.haibin.calendarview.LunarUtil;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BirthUtil {
    /**
     * @param year  年
     * @param month 月
     * @param day   日
     * @param code  {@link SPECIAL_BIRTHDAY}
     * @return [0]年 [1]月 [2]日
     */
    public static List<int[]> calculateBirthday(int year, int month, int day, int code) {
        List<int[]> list = new ArrayList<>();
        for (int i = year; i < 2100; i++) {
            switch (code) {
                case SPECIAL_BIRTHDAY.NONE:
                    list.add(new int[]{i, month, day});
                    break;
                case SPECIAL_BIRTHDAY.BEFORE:
                    list.add(new int[]{i, 2, (isLeapYear(i) ? 29 : 28)});
                    break;
                case SPECIAL_BIRTHDAY.AFTER:
                    list.add(isLeapYear(i) ? new int[]{i, 2, 29} : new int[]{i, 3, 1});
                    break;
                case SPECIAL_BIRTHDAY.ONLY:
                    if (isLeapYear(i)) list.add(new int[]{i, 2, 29});
                    break;
                case SPECIAL_BIRTHDAY.LUNAR:
                    list.add(LunarUtil.lunarToSolar(i, month, day, month < 0));
                    break;
                case SPECIAL_BIRTHDAY.NORMAL:
                    list.add(LunarUtil.lunarToSolar(i,
                            Util.getMonthLeapByYear(i) == -month ? month : -month,
                            day, month < 0));
                case SPECIAL_BIRTHDAY.ONLY_LEAP:
                    if (Util.getMonthLeapByYear(i) == month)
                        list.add(LunarUtil.lunarToSolar(i, month, day, month < 0));
                case SPECIAL_BIRTHDAY.LEAP_AND_NORMAL:
                    if (Util.getMonthLeapByYear(i) == month)
                        list.add(LunarUtil.lunarToSolar(i, -month, day, month < 0));
                    list.add(LunarUtil.lunarToSolar(i, month, day, month < 0));
            }
        }
        return list;
    }

    /**
     * @param year 年份
     * @return 是否为公历闰年
     */
    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    /**
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 距离今日的差值 {@link #diff(LocalDate, LocalDate)}
     */
    public static int[] diff(int year, int month, int day) {
        return diff(LocalDate.of(year, month, day), LocalDate.now());
    }

    public static int[] diff(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay) {
        return diff(LocalDate.of(fromYear, fromMonth, fromDay), LocalDate.of(toYear, toMonth, toDay));
    }

    /**
     * @param fromDate 起始日期
     * @param toDate   结束日期
     * @return [0]年份差值 [1]月份差值 [2]天数差值 [3]1为{@param fromDate}大于{@param toDate},0为等于,-1为小于
     */
    public static int[] diff(LocalDate fromDate, LocalDate toDate) {
        Period period = Period.between(fromDate, toDate);
        return new int[]{Math.abs(period.getYears()), Math.abs(period.getMonths()), Math.abs(period.getDays()),
                fromDate.compareTo(toDate)};
    }

    /**
     * 获取随机颜色
     *
     * @return 以下几种颜色中的一种
     */
    public static int getRandomColor() {
        int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }

    /**
     * @param year    年
     * @param month   月
     * @param day     日
     * @param isLunar 是否为阴历
     * @return 格式化后的日期字符串
     */
    public static String formatDate(int year, int month, int day, boolean isLunar) {
        if (isLunar) return formatLunarDate(year, month, day);
        else return formatDate(year, month, day);
    }

    public static String formatDate(int year, int month, int day) {
        return year + "-" + month + "-" + day;
    }

    public static String formatLunarDate(int year, int month, int day) {
        return Util.getLunarNameOfYear(year) + "年"
                + (month < 0 ? "闰" : "") + Util.getLunarNameOfMonth(Math.abs(month)) + "月"
                + Util.getLunarNameOfDay(day);
    }

    public static String formatToLunarDate(int year, int month, int day) {
        int[] ints = LunarUtil.solarToLunar(year, month, day);
        return formatLunarDate(ints[0], ints[1], ints[2]);
    }

    /**
     * 特殊生日代码
     */
    public static final class SPECIAL_BIRTHDAY {
        /**
         * 公历普通生日
         */
        public static final int NONE = 0;
        /**
         * 公历2/29生日,平年过2/28
         */
        public static final int BEFORE = 1;
        /**
         * 公历2/29生日,平年过2/29
         */
        public static final int AFTER = 2;
        /**
         * 公历2/29生日,平年不过
         */
        public static final int ONLY = 3;
        /**
         * 农历普通生日
         */
        public static final int LUNAR = 4;
        /**
         * 农历闰月生日,闰年过闰月生日,平年过平月
         */
        public static final int NORMAL = 5;
        /**
         * 农历闰月生日,只过闰月生
         */
        public static final int ONLY_LEAP = 6;
        /**
         * 农历闰月生日,闰年过两个生日,平年过平月
         */
        public static final int LEAP_AND_NORMAL = 7;
    }
}
