package com.dev-share.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

/**
 * <pre>
 * 项目:aliyun
 * 描述:访问令牌工具类3.0
 * 作者:ZhangYi
 * 时间:2016年3月29日 下午2:45:00
 * 版本:wsm_v3.5
 * JDK:1.7.80
 * </pre>
 */
public class AccessTokenUtils {
	private static Logger	logger			= Logger.getLogger(AccessTokenUtils.class);
	/**
	 * token加密key(2.0:wafer,3.0:wafer123wafer123)
	 */
	public static String	TOKEN_KEY		= "wafer123wafer123";
	/**
	 * token分隔符(2.0与3.0一致)
	 */
	public static String	TOKEN_DIVIDER	= "=";
	/**
	 * token标志:(2.0:woc,3.0:HELPER/VCALL)
	 */
	public static String	TOKEN_MAEK		= "HELPER";
	/**
	 * token版本
	 */
	public static Integer	TOKEN_VERSION	= 3;

	/**
	 * <pre>
	 * 描述:加密生成Token(Token规则:UID)
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午5:34:36
	 * 参数：(参数列表)
	 * @param uid	用户userId
	 * @return
	 * </pre>
	 */
	public static String encryptToken(String uid) {
		String token = uid;
		if (TOKEN_VERSION == 3) {
			token += TOKEN_DIVIDER + StringUtil.randomNumber(6, true);
		}
		return encrypt(token, TOKEN_KEY);
	}

	/**
	 * <pre>
	 * 描述:加密生成Token(Token规则:UIDPWD)
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午5:35:39
	 * 参数：(参数列表)
	 * @param uid		用户userId
	 * @param password	用户密码
	 * @return
	 * </pre>
	 */
	public static String encryptToken(String uid, String password) {
		String token = uid + TOKEN_DIVIDER + password;
		if (TOKEN_VERSION == 3) {
			token += TOKEN_DIVIDER + StringUtil.randomNumber(6, true);
		}
		return encrypt(token, TOKEN_KEY);
	}

	/**
	 * <pre>
	 * 描述:加密生成Token(Token规则:FULL)
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午5:35:46
	 * 参数：(参数列表)
	 * @param uid			用户userId
	 * @param clientType	客户端类型
	 * @param version		客户端版本
	 * @return
	 * </pre>
	 */
	public static String encryptToken(String uid, String clientType, String version) {
		if (StringUtil.isEmptyStr(clientType)) {
			clientType = "pc";
		}
		if (StringUtil.isEmptyStr(version)) {
			version = "3.5";
		}
		String token = uid + TOKEN_DIVIDER + clientType + TOKEN_DIVIDER + version;
		if (TOKEN_VERSION == 3) {
			token += TOKEN_DIVIDER + StringUtil.randomNumber(6, true) + TOKEN_DIVIDER + version;
		} else {
			token += TOKEN_DIVIDER + TOKEN_MAEK;
		}
		return encrypt(token, TOKEN_KEY);
	}

	/**
	 * <pre>
	 * 描述:解密Token值
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午5:38:33
	 * 参数：(参数列表)
	 * @param token
	 * @return
	 * </pre>
	 */
	public static String decryptToken(String token) {
		String result = "";
		if (!StringUtil.isEmptyStr(token)) {
			String data = decrypt(token, TOKEN_KEY);
			if (!StringUtil.isEmptyStr(data)) {
				String[] datas = data.split(TOKEN_DIVIDER);
				if (datas != null && datas.length >= 1) {
					result = datas[0];
				}
			}
		}
		return result;
	}

	/**
	 * <pre>
	 * 描述:检测token，检测较为简单，只要解密出的token字符串符合系统设定的token组成格式即可，后期可对该检测方法进行优化
	 * 作者:ZhangYi
	 * 时间:2015年9月15日 下午6:20:20
	 * 参数：(参数列表)
	 * @param token
	 * @return
	 * </pre>
	 */
	public static boolean checkToken(String token) {
		if (!StringUtil.isEmptyStr(token) && token.length() > 0 && !"undefined".equals(token)) {
			String data = decrypt(token, TOKEN_KEY);
			if (!StringUtil.isEmptyStr(data)) {
				String[] datas = data.split(TOKEN_DIVIDER);
				if (datas != null && datas.length >= 1) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <pre>
	 * 描述:通过token获取客户端类型
	 * 作者:ZhangYi
	 * 时间:2015年9月15日 下午6:21:25
	 * 参数：(参数列表)
	 * @param token
	 * @return
	 * </pre>
	 */
	public static String getClientType(String token) {
		String result = "";
		if (!StringUtil.isEmptyStr(token)) {
			String data = decrypt(token, TOKEN_KEY);
			if (!StringUtil.isEmptyStr(data)) {
				String[] datas = data.split(TOKEN_DIVIDER);
				if (datas != null && datas.length >= 2) {
					result = datas[1];
				}
			}
		}
		return result;
	}

	/**
	 * <pre>
	 * 描述:通过token获取版本号
	 * 作者:ZhangYi
	 * 时间:2015年9月15日 下午6:22:04
	 * 参数：(参数列表)
	 * @param token
	 * @return
	 * </pre>
	 */
	public static String getVersion(String token) {
		String result = "";
		if (!StringUtil.isEmptyStr(token)) {
			String data = decrypt(token, TOKEN_KEY);
			if (!StringUtil.isEmptyStr(data)) {
				String[] datas = data.split(TOKEN_DIVIDER);
				if (datas != null && datas.length >= 3) {
					result = datas[2];
				}
			}
		}
		return result;
	}

	/**
	 * <pre>
	 * 描述:token加密算法
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午3:33:30
	 * 参数：(参数列表)
	 * @param token	加密数据
	 * @param key	加密key
	 * @return
	 * </pre>
	 */
	protected static String encrypt(String token, String key) {
		String result = "";
		if (StringUtil.isEmptyStr(token) || StringUtil.isEmptyStr(key)) {
			return result;
		}
		try {
			if (TOKEN_VERSION == 3) {/* 使用3.0版本token进行认证 */
				// 判断Key是否为16位
				if (key.length() != 16) {
					return result;
				}
				byte[] raw = key.getBytes();
				SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
				IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
				byte[] encrypted = cipher.doFinal(token.getBytes()); // parseByte2HexStr(result)
				return byteToHex(encrypted);
			} else {
				KeyGenerator _generator = KeyGenerator.getInstance("AES");
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				secureRandom.setSeed(key.getBytes());
				_generator.init(128, secureRandom);
				SecretKey secretKey = _generator.generateKey();
				byte[] enCodeFormat = secretKey.getEncoded();
				SecretKeySpec skey = new SecretKeySpec(enCodeFormat, "AES");
				Cipher cipher = Cipher.getInstance("AES"); // 创建密码器
				byte[] byteContent = token.getBytes("utf-8");
				cipher.init(Cipher.ENCRYPT_MODE, skey); // 初始化
				byte[] data = cipher.doFinal(byteContent);
				String encryptResultStr = byteToHex(data);
				return encryptResultStr; // 加密
			}
		} catch (Exception e) {
			logger.error("--Token加密失败!", e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 描述:二进制转十六进制(加密算法使用)
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午3:34:43
	 * 参数：(参数列表)
	 * @param buf
	 * @return
	 * </pre>
	 */
	private static String byteToHex(byte[] data) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(data[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * <pre>
	 * 描述:token解密算法
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午3:35:29
	 * 参数：(参数列表)
	 * @param token	解密数据
	 * @param key	解密key
	 * @return
	 * </pre>
	 */
	protected static String decrypt(String token, String key) {
		String result = "";
		if (StringUtil.isEmptyStr(token) || StringUtil.isEmptyStr(key)) {
			return result;
		}
		try {
			try {
				logger.info("-------------Token3.0解密-------------");
				key = "wafer123wafer123";
				byte[] raw = key.getBytes("ASCII");
				SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
				byte[] source = hexToByte(token);
				byte[] target = cipher.doFinal(source);
				return new String(target);
			} catch (Exception e) {
				logger.info("-------------Token2.0解密-------------");
				key = "wafer";
				KeyGenerator _generator = KeyGenerator.getInstance("AES");
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				secureRandom.setSeed(key.getBytes());
				_generator.init(128, secureRandom);
				SecretKey secretKey = _generator.generateKey();
				byte[] enCodeFormat = secretKey.getEncoded();
				SecretKeySpec skey = new SecretKeySpec(enCodeFormat, "AES");
				Cipher cipher = Cipher.getInstance("AES"); // 创建密码器
				cipher.init(Cipher.DECRYPT_MODE, skey); // 初始化
				byte[] source = hexToByte(token);
				byte[] target = cipher.doFinal(source);
				return new String(target, "utf-8"); // 加密
			}
		} catch (Exception e) {
			logger.error("--Token解密失败!", e);
		}
		return result;
	}

	/**
	 * <pre>
	 * 描述:token解密算法
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午3:35:29
	 * 参数：(参数列表)
	 * @param token	解密数据
	 * @param key	解密key
	 * @return
	 * </pre>
	 */
	protected static String _decrypt(String token, String key) {
		String result = "";
		if (StringUtil.isEmptyStr(token) || StringUtil.isEmptyStr(key)) {
			return result;
		}
		try {
			if (TOKEN_VERSION == 3) {/* 使用3.0版本token进行认证 */
				byte[] raw = key.getBytes("ASCII");
				SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
				byte[] source = hexToByte(token);
				byte[] target = cipher.doFinal(source);
				return new String(target);
			} else {
				KeyGenerator _generator = KeyGenerator.getInstance("AES");
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				secureRandom.setSeed(key.getBytes());
				_generator.init(128, secureRandom);
				SecretKey secretKey = _generator.generateKey();
				byte[] enCodeFormat = secretKey.getEncoded();
				SecretKeySpec skey = new SecretKeySpec(enCodeFormat, "AES");
				Cipher cipher = Cipher.getInstance("AES"); // 创建密码器
				cipher.init(Cipher.DECRYPT_MODE, skey); // 初始化
				byte[] source = hexToByte(token);
				byte[] target = cipher.doFinal(source);
				return new String(target, "utf-8"); // 加密
			}
		} catch (Exception e) {
			logger.error("--Token解密失败!", e);
		}
		return result;
	}

	/**
	 * <pre>
	 * 描述:十六进制转二进制(解密算法使用)
	 * 作者:ZhangYi
	 * 时间:2016年3月29日 下午3:36:10
	 * 参数：(参数列表)
	 * @param hexStr
	 * @return
	 * </pre>
	 */
	private static byte[] hexToByte(String hex) {
		if (StringUtil.isEmptyStr(hex) || hex.length() < 1) {
			return null;
		}
		byte[] result = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length() / 2; i++) {
			int high = Integer.parseInt(hex.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hex.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static void main(String[] args) {
		String userId = "zhangyi@dev-share.com";
		String token = "";
		token = encryptToken(userId, "pc", "4.0");
		System.out.println(token);
		token = "E2327CDED0C6968438F1A1A936FE0B6A75121213470985B173B02066A5B0269E2A29598CF11D070BB8728088BFA01AC2";
		System.out.println(decryptToken(token));
		token = "C16CEF0180A1BAA8F2CB1DEAAB002DD103225C579CD29B620E92666E3D62F69FED4A1956397CFBBCF8E500205EA20F33";
		System.out.println(decryptToken(token));
	}
}
