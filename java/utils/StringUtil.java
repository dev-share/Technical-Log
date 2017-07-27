package com.share.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * @decription 字符串工具类
 * @author yi.zhang
 * @time 2017年7月27日 下午4:16:06
 * @since 1.0
 * @jdk	1.8
 */
public class StringUtil {
	private final static Logger	logger				= Logger.getLogger(StringUtil.class);
	/**
	 * 分隔符:并号(&)
	 */
	public final static String	DELIMITER_AND		= "&";
	/**
	 * 分隔符:破折号(-)
	 */
	public final static String	DELIMITER_DASH		= "-";
	/**
	 * 分隔符:下划线(_)
	 */
	public final static String	DELIMITER_UNDERLINE	= "_";
	/**
	 * 分隔符:逗号(,)
	 */
	public final static String	DELIMITER_COMMA		= ",";
	/**
	 * 分隔符:点号(.)
	 */
	public final static String	DELIMITER_POINT		= ".";
	/**
	 * 分隔符:冒号(:)
	 */
	public final static String	DELIMITER_COLON		= ":";
	/**
	 * 分隔符:分号(;)
	 */
	public final static String	DELIMITER_SEMICOLON	= ";";

	/**
	 * 编码方式:UTF-8
	 */
	public final static String	ENCODING_UTF8		= "UTF-8";
	/**
	 * 编码方式:UTF-16
	 */
	public final static String	ENCODING_UTF16		= "UTF-16";
	/**
	 * 编码方式:GBK
	 */
	public final static String	ENCODING_GBK		= "GBK";
	/**
	 * 编码方式:GB2312
	 */
	public final static String	ENCODING_GB2312		= "GB2312";
	/**
	 * 编码方式:ISO8859-1
	 */
	public final static String	ENCODING_ISO8859_1	= "ISO8859_1";
//	/**
//	 * (数字+字母)字符库
//	 */
//	private static String[]		CHARACTER_LIBRARY	= new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "A", "b", "B", "c", "C", "d", "D", "e", "E", "f", "F", "g", "G", "h", "H", "i", "I", "j", "J", "k", "K", "l", "L", "m", "M", "n", "N", "o", "O", "p", "P", "q", "Q", "r", "R", "s", "S", "t", "T", "u", "U", "v", "V", "w", "W", "x", "X", "y", "Y", "z", "Z" };

	/**
	 * <pre>
	 * 描述:判断空字符串(空指针、空字符串、"null"字符串)
	 * 作者:ZhangYi
	 * 时间:2015年9月11日 下午4:39:14
	 * 参数：(参数列表)
	 * @param str	目标字符串
	 * @return	(true为空字符串,false不为空字符串)
	 * </pre>
	 */
	public static boolean isEmptyStr(String str) {
		if (null == str || "".equals(str.trim()) || "null".equals(str.trim().toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <pre>
	 * 描述:格式化字符串
	 * 作者:ZhangYi
	 * 时间:2015年9月11日 下午4:42:55
	 * 参数：(参数列表)
	 * @param str	目标字符串
	 * @return
	 * </pre>
	 */
	public static String formatStr(String str) {
		if (null == str || str.equals("null")) {
			return "";
		} else {
			return str.trim();
		}
	}

	/**
	 * <pre>
	 * 描述:字符串校验(特殊字符:下划线/中划线/点/逗号/分号/小括号/空格/&)
	 * 作者:ZhangYi
	 * 时间:2015年9月11日 下午4:50:15
	 * 参数：(参数列表)
	 * @param value	目标字符串
	 * @param type	校验类型(-1.字母+数字+汉字+特殊字符;0.数字,1.字母;2.字母+数字;3.字母+数字+特殊字符;4.汉字+空格)
	 * @return
	 * </pre>
	 */
	public static boolean checkStr(String value, int type) {
		String reg = "^[-a-zA-Z0-9_\u4e00-\u9fa5\\-\\. &,;()；，（）]+$";
		if (type == 0) {// 数字
			reg = "^[0-9]+$";
		}
		if (type == 1) {// 字母
			reg = "^[a-zA-Z]+$";
		}
		if (type == 2) {// 字母+数字
			reg = "^[a-zA-Z0-9]+$";
		}
		if (type == 3) {// 字母+数字+特殊字符
			reg = "^[a-zA-Z0-9_\\-\\. &,;()；，（）]+$";
		}
		if (type == 4) {// 汉字+空格
			reg = "^[\u4e00-\u9fa5 ]+$";
		}
		if (isEmptyStr(value)) {
			return false;
		} else {
			if (value.matches(reg)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * <pre>
	 * 描述:电话号码或手机号码匹配(例如:029-829217,829217,13688888888,+8613688888888)
	 * 作者:ZhangYi
	 * 时间:2016年3月10日 上午11:03:34
	 * 参数：(参数列表)
	 * @param tel	电话号码或手机号码
	 * @return
	 * </pre>
	 */
	public static boolean telMatches(String tel) {
		String reg = "(^[1-9]{1}([0-9]{6,7})$|^[0]([1-9]{2})([-]?)([0-9]{8})$|^[0]([1-9]{3})([-]?)([0-9]{7}))$|(^[1]([3458]{1})([0-9]{9})$|^[+]?([8]{1}[6]{1})([1][3458]{1})([0-9]{9})$)";
		if (isEmptyStr(tel)) {
			return false;
		} else {
			return tel.matches(reg);
		}
	}

	/**
	 * <pre>
	 * 描述:统计字符串长度
	 * 作者:ZhangYi
	 * 时间:2015年9月11日 下午5:31:26
	 * 参数：(参数列表)
	 * @param value
	 * @return
	 * </pre>
	 */
	public static int countLength(String value) {
		int len = 0;
		String chinese = "[\u0391-\uFFE5]";
		/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		for (int i = 0; i < value.length(); i++) {
			/* 获取一个字符 */
			String temp = value.substring(i, i + 1);
			/* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
				len += 2;
			} else {
				/* 其他字符长度为1 */
				len += 1;
			}
		}
		return len;
	}

	/**
	 * <pre>
	 * 描述:截取一段字符(不区分中英文,如果数字不正好，则少取一个字符位)
	 * 作者:ZhangYi
	 * 时间:2015年9月11日 下午5:36:48
	 * 参数：(参数列表)
	 * @param value		原始字符串 
	 * @param len		截取长度(一个汉字长度按2算的) 
	 * @return
	 * </pre>
	 */
	public static String substring(String value, int len) {
		if (isEmptyStr(value)) return "";
		byte[] strByte = new byte[len];
		if (len > countLength(value)) {
			return value;
		}
		try {
			System.arraycopy(value.getBytes("GBK"), 0, strByte, 0, len);
			int count = 0;
			for (int i = 0; i < len; i++) {
				int n = (int) strByte[i];
				if (n < 0) {
					count++;
				}
			}
			if (count % 2 != 0) {
				len = (len == 1) ? ++len : --len;
			}
			return new String(strByte, 0, len, "GBK");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <pre>
	 * 描述:提取数字并拼接随机数
	 * 作者:administrator
	 * 时间:2005年9月11日 下午5:49:32
	 * 参数：(参数列表)
	 * @param longStr
	 * @return
	 * </pre>
	 */
	public static String extractionAssemble(String str) {
		str = str.trim();
		String strNum = "";// 从字符串中提取数字
		if (str != null && !"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
					strNum += str.charAt(i);
				}
			}

		}

		long num = Long.parseLong(strNum);
		num = num % 100;
		String numStr = String.valueOf(num);
		int random4Num = (int) (Math.random() * 9000 + 1000);
		numStr += random4Num;
		return numStr;
	}

	/**
	 * <pre>
	 * 描述:过滤重复字符串
	 * 作者:ZhangYi
	 * 时间:2015年9月11日 下午6:00:05
	 * 参数：(参数列表)
	 * @param values 目标数组
	 * @return
	 * </pre>
	 */
	public static String[] filterRepeat(String[] values) {
		TreeSet<String> filter = new TreeSet<String>();
		for (String value : values) {
			if (!isEmptyStr(value)) {
				filter.add(value);
			}
		}
		String[] result = new String[filter.size()];
		filter.toArray(result);
		return result;
	}

	/**
	 * <pre>
	 * 描述:数组转化为字符串
	 * 作者:ZhangYi
	 * 时间:2015年9月11日 下午6:15:36
	 * 参数：(参数列表)
	 * @param values	目标数组
	 * @param flag		转化类型(true:单引号拼接,false:普遍拼接)
	 * @return
	 * </pre>
	 */
	public static String arrayToString(String[] values, boolean flag) {
		String result = "";
		for (String value : values) {
			if (!isEmptyStr(value)) {
				if (isEmptyStr(result)) {
					if (flag) {
						result += "'" + value + "'";
					} else {
						result += value;
					}
				} else {
					if (flag) {
						result += ",'" + value + "'";
					} else {
						result += "," + value;
					}
				}
			}
		}
		return result;
	}

	/**
	 * <pre>
	 * 作者：ZhangYi
	 * 描述：过滤替换多余字符串(替换连续标示)
	 * 时间：2014年8月4日 下午6:43:29
	 * @param target	字符串源
	 * @param replacement	替换标示	
	 * @return
	 * </pre>
	 */
	public static String replaceAll(String target, String replacement) {
		if (target.indexOf(replacement + replacement) == -1) {
			if (target.startsWith(replacement)) {
				target = target.substring(replacement.length());
			}
			if (target.endsWith(replacement)) {
				target = target.substring(0, target.length() - replacement.length());
			}
			return target;
		} else {
			target = target.replaceAll(replacement + replacement, replacement);
		}
		return replaceAll(target, replacement);
	}

	/**
	 * <pre>
	 * 描述:UTF-8转码ISO8859-1
	 * 作者:ZhangYi
	 * 时间:2015年1月22日 下午2:30:05
	 * 参数：(参数列表)
	 * @param str
	 * @return
	 * </pre>
	 */
	public static String utf8ToIso(String str) {
		try {
			return str == null ? null : new String(str.getBytes(ENCODING_UTF8), ENCODING_ISO8859_1);
		} catch (Exception e) {
			logger.error("--UTF8转ISO8859-1错误:" + str, e);
			return null;
		}
	}

	/**
	 * <pre>
	 * 描述:字符串转码UTF-8
	 * 作者:ZhangYi
	 * 时间:2015年1月22日 下午2:31:14
	 * 参数：(参数列表)
	 * @param str
	 * @return
	 * </pre>
	 */
	public static String stringToUTF8(String str) {
		try {
			return str == null ? null : new String(str.getBytes(), ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			logger.error("--字符串转UTF8错误:" + str, e);
			return null;
		}
	}

	/**
	 * <pre>
	 * 描述:字符串编码GBK的二进制
	 * 作者:ZhangYi
	 * 时间:2015年1月22日 下午2:43:14
	 * 参数：(参数列表)
	 * @param str
	 * @return
	 * </pre>
	 */
	public static byte[] stringToGBKByte(String str) {
		try {
			return str.getBytes(ENCODING_GBK);
		} catch (UnsupportedEncodingException e) {
			logger.error("--字符串转GBK错误:" + str, e);
			return null;
		}
	}

	/**
	 * <pre>
	 * 描述:字符串反转
	 * 作者:ZhangYi
	 * 时间:2015年1月22日 下午2:44:59
	 * 参数：(参数列表)
	 * @param str
	 * @return
	 * </pre>
	 */
	public static String reverseString(String str) {
		StringBuffer info = new StringBuffer(str);
		info = info.reverse();
		return info.toString();
	}

	/**
	 * <pre>
	 * 描述:字符串反转(字符反转)
	 * 作者:ZhangYi
	 * 时间:2015年1月22日 下午2:45:36
	 * 参数：(参数列表)
	 * @param str
	 * @return
	 * </pre>
	 */
	public static String reverseString2(String str) {
		char[] ch = str.toCharArray();
		String info = "";
		for (int i = ch.length - 1; i >= 0; i--) {
			info += ch[i];
		}
		return info;
	}

	/**
	 * <pre>
	 * 描述:n位16进制随机数字
	 * 作者:ZhangYi
	 * 时间:2015年1月22日 下午2:51:19
	 * 参数：(参数列表)
	 * @param n
	 * @return
	 * </pre>
	 */
	public static String randomHexNumber(int n) {
		int j = 0;
		String number = "";
		while (j < (n - 1) / 4 + 1) {
			int i = new Random().nextInt(65536);
			String m = Integer.toHexString(i).toUpperCase();
			while (m.length() < 4) {
				m = "0" + m;
			}
			number += m;
			j++;
		}
		return number.substring(number.length() - n);
	}

	/**
	 * <pre>
	 * 描述:n位随机数字
	 * 作者:ZhangYi
	 * 时间:2015年1月22日 下午3:04:41
	 * 参数：(参数列表)
	 * @param len	随机数长度
	 * @param flag	首位是否允许为0(true:首位大于0,false:首位任意数)
	 * @return
	 * </pre>
	 */
	public static String randomNumber(int len, boolean flag) {
		String number = new Random().nextInt(10) + "";
		if (flag) {
			while (number.equals("0")) {
				number = new Random().nextInt(10) + "";
			}
		}
		while (number.length() < len) {
			number += new Random().nextInt(10);
		}
		return number;
	}

	/**
	 * <pre>
	 * 描述:字符串填充0为固定长度,超出则截取
	 * 作者:ZhangYi
	 * 时间:2015年11月17日 下午6:29:07
	 * 参数：(参数列表)
	 * @param source 源字符串
	 * @param len	字符串长度
	 * @param flag	填充位置(true:左填充/右截取,false:右填充/左截取)
	 * @return
	 * </pre>
	 */
	public static String fillString(String source, int len, boolean flag) {
		if (source.length() < len) {
			while (source.length() < len) {
				if (flag) {
					source = "0" + source;
				} else {
					source += "0";
				}
			}
		} else {
			if (flag) {
				source = source.substring(source.length() - len);
			} else {
				source = source.substring(0, len);
			}
		}
		return source;
	}

	public static void main(String[] args) {
		System.out.println(fillString("1001", 3, false));
	}
}
