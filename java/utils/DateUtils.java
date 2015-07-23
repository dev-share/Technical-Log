package com.zy.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * <pre>
 * 项目:SmartCheckApproval
 * 描述:日期工具类
 * 作者:ZhangYi
 * 时间:2015年1月12日 下午3:53:15
 * 版本:wsm_v3.1
 * JDK:1.7.65
 * </pre>
 */
public class DateUtils {
	/**
	 * <pre>
	 * 描述:[日期型]获取当前日期(例如:2015-03-18)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:40:02
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 */
	public static Date getNowDate() {
		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return calendar.getTime();
	}
	/**
	 * <pre>
	 * 描述:[日期型]获取当前时间(例如:2015-03-18 15:30:56)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:40:02
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 */
	public static Date getNowDateAndTime() {
		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
		return calendar.getTime();
	}

	/**
	 * <pre>
	 * 描述:[字符串型]获取当前日期(例如:2015-03-18)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:40:02
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 */
	public static String getNowDateStr() {
		return DateUtils.convertToDateStr(getNowDate());
	}
	/**
	 * <pre>
	 * 描述:[字符串型]获取当前时间(例如:2015-03-18 15:30:56)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:40:02
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 */
	public static String getNowDateAndTimeStr() {
		return DateUtils.convertToDateAndTimeStr(getNowDateAndTime());
	}
	/**
	 * <pre>
	 * 描述:[字符串型]获取当前时刻(例如:15:30:56)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:40:02
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 */
	public static String getNowTimeStr() {
		return DateUtils.getNowDateAndTimeStr().split(" ")[1];
	}

	/**
	 * <pre>
	 * 描述:更具给定的pattern时间形式进行时间date的字符串转换
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:49:00
	 * 参数：(参数列表)
	 * @param locale	本地语言对象
	 * @param pattern	格式
	 * @param date		需要转换日期
	 * @return
	 * </pre>
	 */
	public static String getDateStrByPattern(Locale locale, String pattern, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
		return dateFormat.format(date);
	}
	/**
	 * <pre>
	 * 描述:[字符串型]日期型转字符串(例如:2015-03-18)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:40:02
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 */
	public static String convertToDateStr(Date date) {
		if (date == null) return null;
		DateFormat formatParse = new SimpleDateFormat("yyyy-MM-dd");
		return formatParse.format(date);
	}
	/**
	 * <pre>
	 * 描述:[字符串型]日期型转字符串(例如:2015-03-18 08:55:07)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:55:07
	 * 参数：(参数列表)
	 * @param date
	 * @return
	 * </pre>
	 */
	public static String convertToDateAndTimeStr(Date date) {
		if (date == null) return null;
		DateFormat formatParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatParse.format(date);
	}
	/**
	 * <pre>
	 * 描述:[日期型]字符串转日期型(例如:2015-03-18)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:40:02
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 * @throws Exception 
	 */
	public static Date convertToDate(String dateStr){
		if (StringUtils.isNullOrEmpty(dateStr)) return null;
		DateFormat formatParse = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return formatParse.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("字符串转日期型错误,字符串格式必须为'yyyy-MM-dd',参数:"+dateStr);
		}
	}
	/**
	 * <pre>
	 * 描述:[日期型]字符串转日期型(例如:2015-03-18 08:55:07)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:55:07
	 * 参数：(参数列表)
	 * @param date
	 * @return
	 * </pre>
	 */
	public static Date convertToDateAndTime(String dateStr) {
		if (StringUtils.isNullOrEmpty(dateStr)) return null;
		DateFormat formatParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return formatParse.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("字符串转日期型错误,字符串格式必须为'yyyy-MM-dd HH:mm:ss',参数:"+dateStr);
		}
	}
	/**
	 * <pre>
	 * 描述:[日期型]毫秒转日期型(例如:2015-03-18)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:55:07
	 * 参数：(参数列表)
	 * @param ms	毫秒数
	 * @return
	 * </pre>
	 */
	public static Date convertToDateStr(long time) throws ParseException {
		String dateStr = convertToDateStr(new Date(time));
		Date date = convertToDate(dateStr);
		return date;
	}
	/**
	 * <pre>
	 * 描述:[日期型]毫秒转日期型(例如:2015-03-18 08:55:07)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午1:55:07
	 * 参数：(参数列表)
	 * @param ms	毫秒数
	 * @return
	 * </pre>
	 */
	public static Date convertToDateAndTime(long time) throws ParseException {
		String dateStr = convertToDateAndTimeStr(new Date(time));
		Date date = convertToDateAndTime(dateStr);
		return date;
	}
	/**
	 * <pre>
	 * 描述:[日期型]字符串转指定格式日期型(例如:2015-03-18 08:55:07)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午2:21:11
	 * 参数：(参数列表)
	 * @param  dateStr	日期字符串
	 * @param  pattern	日期格式
	 * @return
	 * </pre>
	 */
	public static Date convertToDateOrDateTime(String dateStr, String  pattern) {
		if (StringUtils.isNullOrEmpty(dateStr)) return null;
		DateFormat formatParse = new SimpleDateFormat(pattern);
		try {
			return formatParse.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("字符串转日期型错误,字符串格式必须为:"+pattern+",参数:"+dateStr);
		}
	}
	/**
	 * <pre>
	 * 描述:[日期型]获取日期(例如:2015-03-21 14:16:42)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午2:51:47
	 * 参数：(参数列表)
	 * @param year		年
	 * @param month		月
	 * @param day		日
	 * @param hour		时
	 * @param minute	分
	 * @param second	秒
	 * @return
	 * </pre>
	 */
	public static Date getDateTime(int year,int month,int day,int hour,int minute,int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTime();
	}
	/**
	 * <pre>
	 * 描述:[日期型]获取日期(例如:2015-03-21)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午2:51:47
	 * 参数：(参数列表)
	 * @param year		年
	 * @param month		月
	 * @param day		日
	 * @return
	 * </pre>
	 */
	public static Date getDate(int year,int month,int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	/**
	 * <pre>
	 * 描述:[日期型]获取某年年初第一天(例如:2015-01-01 00:00:01)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午2:37:31
	 * 参数：(参数列表)
	 * @param year
	 * @return
	 * </pre>
	 */
	public static Date getFristDate(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return calendar.getTime();
	}
	
	/**
	 * <pre>
	 * 描述:[日期型]获取某年年末最后一天(例如:2015-12-31 23:59:59)
	 * 作者:ZhangYi
	 * 时间:2015年3月18日 下午2:37:31
	 * 参数：(参数列表)
	 * @param year
	 * @return
	 * </pre>
	 */
	public static Date getLastDate(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
		return calendar.getTime();
	}

	/**
	 * <pre>
	 * 描述:[日期型]获取某天的前几日(例如:2015-12-31 23:59:59)
	 * 作者:Tianjian
	 * 时间:2015年4月13日 下午2:05:31
	 * 参数：(参数列表)
	 * @param date
	 * @param n
	 * @return
	 * </pre>
	 * @throws ParseException 
	 */
	public static Date getNextDay(Date date,int n) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH,n);
		date = calendar.getTime();
		
		String dateStr = convertToDateStr(date);
		Date newdate = convertToDate(dateStr);
		return newdate;
	}
	
	public static void main(String[] args) {
		Date d=convertToDateOrDateTime("2015-04-07","yyyy-MM-dd");
		System.out.println(d.getTime());
	}
}
