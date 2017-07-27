package com.share.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Random;

/**
 * @decription 数字工具类
 * @author yi.zhang
 * @time 2017年7月27日 下午4:50:02
 * @since 1.0
 * @jdk	1.8
 */
public class NumberUtil {
	/**
	 * <pre>
	 * 描述:判断字符串是否是数字
	 * 作者:ZhangYi
	 * 时间:2015年3月10日 上午9:28:57
	 * 参数：(参数列表)
	 * @param number
	 * @return
	 * </pre>
	 */
	public static boolean isNumber(String number) {
		if (null == number || "".equals(number.trim()))
			return false;
		else {
			for (int i = 0; i < number.length(); i++) {
				if (number.charAt(i) < '0' || number.charAt(i) > '9')
					return false;
			}

			return true;
		}
	}
	/**
	 * <pre>
	 * 描述:格式化数字
	 * 作者:ZhangYi
	 * 时间:2015年4月1日 下午5:17:54
	 * 参数：(参数列表)
	 * @param obj	目标数字
	 * @param n		小数点后保留位数
	 * @return
	 * </pre>
	 */
	public static double formatNumber(Object obj, int n){
		double number=(Double)obj;
		BigDecimal bt=new BigDecimal(number);
		bt=bt.setScale(n, BigDecimal.ROUND_HALF_UP);
      	return bt.doubleValue();
	}
	/**
	 * <pre>
	 * 描述:格式化数字
	 * 作者:ZhangYi
	 * 时间:2015年3月10日 上午9:36:39
	 * 参数：(参数列表)
	 * @param number 目标双精度数字
	 * @param n		 小数点后保留位数
	 * @return
	 * </pre>
	 */
	public static String formatNumber(double number,int n) {
		String value = "";
		if(n>0){
			value = Double.valueOf(number).intValue()+"";
		}else{
			String format = "#.";
			for(int i=0;i<n;i++){
				format+="0";
			}
			value = new java.text.DecimalFormat(format).format(number);
		}
		return value;
	}
	
	/**
	 * <pre>
	 * 描述:产生浮点类型的随机数
	 * 作者:ZhangYi
	 * 时间:2015年3月10日 上午9:48:40
	 * 参数：(参数列表)
	 * @param from		起始值
	 * @param to		结束值
	 * @param scale		精度（小数位数）
	 * @return
	 * </pre>
	 */
	public static float randomFloat(float from, float to, int scale) {
		if (from >= to)
			return -1F;
		float value = -1F;
		Random random = new Random();

		if ((int) (to - from) > 0)
			value = from + (float) random.nextInt((int) (to - from)); // 整数随机数字
		else
			value = from; // 小数随机数字

		float temp;
		for (temp = to; temp >= to; temp = value + random.nextFloat())
			; // 循环获取第1个小于to的随机小数

		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(scale);

		return Float.parseFloat(nf.format(temp));
	}

	/**
	 * <pre>
	 * 描述:产生整型的随机数
	 * 作者:ZhangYi
	 * 时间:2015年3月10日 上午9:47:52
	 * 参数：(参数列表)
	 * @param from		起始值
	 * @param to		结束值
	 * @return
	 * </pre>
	 */
	public static int randomInt(int from, int to) {
		if (from >= to)
			return -1;
		else {
			int value = -1;
			Random random = new Random();
			// 整数随机数字
			value = from + random.nextInt((int) (to - from));
			return value;
		}
	}

	/**
	 * <pre>
	 * 描述:计算地球上任意两点(经纬度)距离
	 * 作者:ZhangYi
	 * 时间:2015年3月10日 上午9:59:46
	 * 参数：(参数列表)
	 * @param long1		第一点经度 
	 * @param lat1		第一点纬度
	 * @param long2		第二点经度 
	 * @param lat2		第二点纬度
	 * @return			返回距离（单位）：米 
	 * </pre>
	 */
	public static double distance(double lng1, double lat1, double lng2,double lat2) {  
		double x, y;
		double radius = 6371229; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		x = lat1 - lat2;
		y = (lng1 - lng2) * Math.PI / 180.0;
		double distance;
		double X, Y;
		X = Math.sin(x/ 2.0);
		Y = Math.sin(y/ 2.0);
		distance = 2*radius* Math.asin(Math.sqrt(X * X + Math.cos(lat1)* Math.cos(lat2) * Y * Y));
		return distance; 
	}
	/**
	 * 测试方法
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		double lng1=108.88490898358;
		double lat1=34.229565090804;
		double lng2=108.88518278942;
		double lat2=34.22963318813;
		double value = distance(lng1, lat1, lng2, lat2);
		System.out.println(value);
	}
}
