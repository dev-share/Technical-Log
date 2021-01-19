package com.dev-share.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * 描述:WebEx工具类
 * 作者:ZhangYi
 * 时间:2016年7月5日 上午9:29:27
 * 版本:wrm_v4.0
 * JDK:1.7.80
 * </pre>
 */
public class WebExUtils {
	private static final Logger	logger	= Logger.getLogger(WebExUtils.class);

	/**************************************************************************************************************************/
	/******************************************************* WebEx URL API 接口 *************************************************/
	/**************************************************************************************************************************/
	/**
	 * <pre>
	 * 描述:登录WebEx[URL API WBS_31接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月5日 上午10:33:26
	 * 参数：(参数列表)
	 * @param resource	资源对象
	 * @return
	 * </pre>
	 */
	public static String loginWebEx(WebexResource resource) {
		String webex_url = resource.getLoginURL() + "?AT=LI&WID=" + resource.getAccount() + "&PW=" + resource.getPassword();
		String result = "";
		PostMethod httpPost = null;
		try {
			List<NameValuePair> params = new ArrayList<>();
			params.add(new NameValuePair("AT", "LI"));
			params.add(new NameValuePair("WID", resource.getAccount()));
			params.add(new NameValuePair("PW", resource.getPassword()));
			NameValuePair[] _params = new NameValuePair[params.size()];
			params.toArray(_params);
			String login_url = resource.getLoginURL();
			logger.info(login_url);
			httpPost = new PostMethod(login_url);
			if (null != _params) {
				httpPost.addParameters(_params);
			}
			HttpClient httpclient = new HttpClient();
			httpclient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			int code = httpclient.executeMethod(httpPost);
			if (code == 200) {
				result = httpPost.getResponseBodyAsString();
			} else if (code == 302) {
				result = httpPost.getResponseHeader("Location").getValue();
			}
			if (!StringUtil.isEmptyStr(result)) {
				String token = "";
				Pattern csrfParrern = Pattern.compile("\\\\x26CSRF\\\\x3d(.+?)\"");
				Matcher matcher = csrfParrern.matcher(result);
				if (matcher.find()) {
					token = matcher.group(1);
				}
				if (!StringUtil.isEmptyStr(token)) {
					logger.info("--WebEx登录Token:" + token);
				} else {
					logger.info("--WebEx登录结果:" + result);
				}
				return token;
			}
		} catch (Exception e) {
			logger.error("--WebEx登录[URL API WBS_31接口]失败!", e);
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return webex_url;
	}

	/**
	 * <pre>
	 * 描述:查询WebEx列表[URL API WBS_31接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月5日 上午10:33:26
	 * 参数：(参数列表)
	 * @param resource	资源对象
	 * @return
	 * </pre>
	 */
	public static String findAllWebEx(WebexResource resource) {
		String result = "";
		PostMethod httpPost = null;
		try {
			String token = loginWebEx(resource);
			if (!StringUtil.isEmptyStr(token)) {
				List<NameValuePair> params = new ArrayList<>();
				params.add(new NameValuePair("AT", "LM"));
				params.add(new NameValuePair("CSRF", token));
				NameValuePair[] _params = new NameValuePair[params.size()];
				params.toArray(_params);
				String webex_url = resource.getAPIURL();
				logger.info(webex_url);
				httpPost = new PostMethod(webex_url);
				if (null != _params) {
					httpPost.addParameters(_params);
				}
				HttpClient httpclient = new HttpClient();
				httpclient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
				int code = httpclient.executeMethod(httpPost);
				if (code == 200) {
					result = httpPost.getResponseBodyAsString();
				} else if (code == 302) {
					result = httpPost.getResponseHeader("Location").getValue();
				}
				logger.info(code + "--WebEx查询列表:" + result);
			}
		} catch (Exception e) {
			logger.error("--查询WebEx列表[URL API WBS_31接口]失败!", e);
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return result;
	}

	/**
	 * <pre>
	 * 描述:主持WebEx[URL API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月5日 上午10:33:26
	 * 参数：(参数列表)
	 * @param resource	资源对象
	 * @param key		WebEx会议唯一标示
	 * @return
	 * </pre>
	 */
	public static String holdWebEx(WebexResource resource, String key) {
		if (StringUtil.isEmptyStr(key)) {
			return null;
		}
		String webex_url = resource.getAPIURL() + "?AT=HM" + "&MK=" + key;
		return webex_url;
	}

	/**
	 * <pre>
	 * 描述:加入WebEx[URL API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月5日 上午10:42:52
	 * 参数：(参数列表)
	 * @param resource	资源对象
	 * @param email		参与人邮箱
	 * @param userName	参会人姓名
	 * @param key		WebEx会议唯一标示
	 * @return
	 * </pre>
	 */
	public static String joinWebEx(WebexResource resource, String email, String userName, String key) {
		if (StringUtil.isEmptyStr(key)) {
			return null;
		}
		String webex_url = resource.getAPIURL() + "?AT=JM" + "&MK=" + key + "&AE=" + email + "&AN=" + userName;
		return webex_url;
	}

	/**
	 * <pre>
	 * 描述:查询当前正在进行中WebEx会议[URL API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月5日 上午10:52:00
	 * 参数：(参数列表)
	 * @param resource	资源对象
	 * @return
	 * </pre>
	 */
	public static String findCurrentWebExs(WebexResource resource) {
		try {
			HttpClient client = new HttpClient();
			client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			String login_url = resource.getLoginURL();
			logger.info(login_url);
			PostMethod method = new PostMethod(login_url);
			List<NameValuePair> params = new ArrayList<>();
			params.add(new NameValuePair("AT", "LI"));
			params.add(new NameValuePair("WID", resource.getAccount()));
			params.add(new NameValuePair("PW", resource.getPassword()));
			NameValuePair[] _params = new NameValuePair[params.size()];
			method.setRequestBody(params.toArray(_params));
			int status = client.executeMethod(method);
			if (status == 200) {
				Cookie[] cookies = client.getState().getCookies();
				String cookieStr = " ";
				for (Cookie c : cookies) {
					cookieStr += c.getName() + "=" + c.getValue() + "; ";
				}
				logger.info("Cookie is [" + cookieStr + "]");
				params = new ArrayList<>();
				params.add(new NameValuePair("AT", "OM"));
				String api_url = resource.getAPIURL();
				method = new PostMethod(api_url);
				_params = new NameValuePair[params.size()];
				method.setRequestBody(params.toArray(_params));
				method.setRequestHeader("Accept-Language", "zh-cn");
				method.setRequestHeader("Accept-Encoding", " gzip, deflate");
				method.setRequestHeader("If-Modified-Since", "Thu, 29 Jul 2004 02:24:49 GMT");
				method.setRequestHeader("If-None-Match", "'3014d-1d31-41085ff1'");
				method.setRequestHeader("User-Agent", " Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.2)");
				method.setRequestHeader("Host", resource.getSiteURL());
				method.setRequestHeader("Connection", " Keep-Alive");
				method.setRequestHeader("Cookie", cookieStr);
				status = client.executeMethod(method);
				String result = method.getResponseBodyAsString();
				logger.info("--查询当前时间段内WebEx会议:" + result);
				if (!StringUtil.isEmptyStr(result) && result.contains("<Status>")) {
					String info = result.substring(result.indexOf("<Status>") + "<Status>".length(), result.indexOf("</Status>"));
					if (info.contains("SUCCESS")) {
						String meetingKey = result.substring(result.indexOf("<MeetingKeys>") + "<MeetingKeys>".length(), result.indexOf("</MeetingKeys>"));
						result = "0:" + (StringUtil.isEmptyStr(meetingKey) ? " " : meetingKey);
					}
					if (info.contains("FAIL")) {
						String reason = result.substring(result.indexOf("<Reason>") + "<Reason>".length(), result.indexOf("</Reason>"));
						result = "1:" + reason;
					}
					return result;
				}
			}
		} catch (Exception e) {
			logger.error("--查询当前正在进行中WebEx会议[URL API接口]失败!", e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 描述:同步WebEx会议[URL API接口]
	 * 作者:ZhangYi
	 * 时间:2016年6月30日 上午10:29:41
	 * 参数：(参数列表)
	 * @param meeting	会议内容
	 * @param resource	资源对象
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static String synWebEx(Meeting meeting, WebexResource resource) throws Exception {
		String key = "";
		try {
			HttpClient client = new HttpClient();
			client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			String login_url = resource.getLoginURL();
			logger.info(login_url);
			PostMethod method = new PostMethod(login_url);
			List<NameValuePair> params = new ArrayList<>();
			params.add(new NameValuePair("AT", "LI"));
			params.add(new NameValuePair("WID", resource.getAccount()));
			params.add(new NameValuePair("PW", resource.getPassword()));
			NameValuePair[] _params = new NameValuePair[params.size()];
			method.setRequestBody(params.toArray(_params));
			int status = client.executeMethod(method);
			if (status == 200) {
				Cookie[] cookies = client.getState().getCookies();
				String cookieStr = " ";
				for (Cookie c : cookies) {
					cookieStr += c.getName() + "=" + c.getValue() + "; ";
				}
				logger.info("Cookie is [" + cookieStr + "]");
				params = new ArrayList<>();
				if (StringUtil.isEmptyStr(meeting.getOid())) {
					params.add(new NameValuePair("AT", "SM"));
				} else {
					params.add(new NameValuePair("AT", "EM"));
					params.add(new NameValuePair("MK", meeting.getOid()));
				}
				params.add(new NameValuePair("MN", URLEncoder.encode(meeting.getName(), "UTF-8")));
				params.add(new NameValuePair("PW", "wafer"));
				params.add(new NameValuePair("DU", DateUtil.intervalMinutes(meeting.getStartTime(), meeting.getEndTime()) + ""));
				params.add(new NameValuePair("YE", DateUtil.formatYear(meeting.getStartTime())));
				params.add(new NameValuePair("MO", DateUtil.formatMonth(meeting.getStartTime())));
				params.add(new NameValuePair("DA", DateUtil.formatDay(meeting.getStartTime())));
				params.add(new NameValuePair("HO", DateUtil.formatDateTimeStr(meeting.getStartTime(), "HH")));
				params.add(new NameValuePair("MI", DateUtil.formatDateTimeStr(meeting.getStartTime(), "mm")));
				params.add(new NameValuePair("IP", "1"));
				String api_url = resource.getAPIURL();
				method = new PostMethod(api_url);
				_params = new NameValuePair[params.size()];
				method.setRequestBody(params.toArray(_params));
				method.setRequestHeader("Accept-Language", "zh-cn");
				method.setRequestHeader("Accept-Encoding", " gzip, deflate");
				method.setRequestHeader("If-Modified-Since", "Thu, 29 Jul 2004 02:24:49 GMT");
				method.setRequestHeader("If-None-Match", "'3014d-1d31-41085ff1'");
				method.setRequestHeader("User-Agent", " Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.2)");
				method.setRequestHeader("Host", resource.getSiteURL());
				method.setRequestHeader("Connection", " Keep-Alive");
				method.setRequestHeader("Cookie", cookieStr);
				status = client.executeMethod(method);
				String result = method.getResponseBodyAsString();
				if (!StringUtil.isEmptyStr(result) && result.contains("<script language='javascript'>")) {
					logger.info(result);
					result = result.substring(result.lastIndexOf("<script language='javascript'>"), result.lastIndexOf("</script>"));
					String redirect_url = result.substring(result.indexOf("url = \"") + ("url = ".length()) + 1, result.indexOf("\";"));
					redirect_url = redirect_url.replace("\\x3a", ":").replace("\\x2f", "/").replace("\\x3f", "?").replace("\\x3d", "=").replace("\\x26", "&").replace("\\x25", "%");
					logger.info(URLDecoder.decode(redirect_url, "UTF-8"));
					method = new PostMethod(URLDecoder.decode(redirect_url, "UTF-8"));
					status = client.executeMethod(method);
				}
				Header header = method.getResponseHeader("location");
				if ((status == HttpStatus.SC_MOVED_TEMPORARILY) || (status == HttpStatus.SC_MOVED_PERMANENTLY)
						|| (status == HttpStatus.SC_SEE_OTHER) || (status == HttpStatus.SC_TEMPORARY_REDIRECT)) {
					// 获取重定向后的URL地址
					if (header != null && !StringUtil.isEmptyStr(header.getValue())) {
						String uri = header.getValue();
						if (uri.contains("MK")) {
							key = uri.substring(uri.lastIndexOf("MK=") + 3);
						}
					}
				} else if (status == HttpStatus.SC_OK) {
					String uri = method.getURI().getURI();
					if (uri.contains("MK")) {
						key = uri.substring(uri.lastIndexOf("MK=") + 3);
					}
					String info = "";
					if (uri.contains("ST=")) {
						info = uri.substring(uri.lastIndexOf("ST=") + 3);
						info = info.substring(0, info.indexOf("&"));
						info += "----->" + key;
					}
					if (uri.contains("RS=")) {
						info += ":" + uri.substring(uri.lastIndexOf("RS=") + 3);
					}
					logger.info("--WebEx会议--" + info);
				}
			}
			logger.info("--WebEx会议唯一标示:" + key);
		} catch (Exception e) {
			logger.error("--同步WebEx会议[URL API接口]失败!", e);
		}
		return key;
	}

	/**
	 * <pre>
	 * 描述:删除WebEx会议[URL API接口]
	 * 作者:ZhangYi
	 * 时间:2016年6月30日 上午10:29:41
	 * 参数：(参数列表)
	 * @param key		WebEx唯一标示
	 * @param resource	资源对象
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static boolean deleteWebEx(String oid, WebexResource resource) throws Exception {
		try {
			HttpClient client = new HttpClient();
			client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			String login_url = resource.getLoginURL();
			logger.info(login_url);
			PostMethod method = new PostMethod(login_url);
			List<NameValuePair> params = new ArrayList<>();
			params.add(new NameValuePair("AT", "LI"));
			params.add(new NameValuePair("WID", resource.getAccount()));
			params.add(new NameValuePair("PW", resource.getPassword()));
			NameValuePair[] _params = new NameValuePair[params.size()];
			method.setRequestBody(params.toArray(_params));
			int status = client.executeMethod(method);
			if (status == 200) {
				Cookie[] cookies = client.getState().getCookies();
				String cookieStr = " ";
				for (Cookie c : cookies) {
					cookieStr += c.getName() + "=" + c.getValue() + "; ";
				}
				logger.info("Cookie is [" + cookieStr + "]");
				params = new ArrayList<>();
				params.add(new NameValuePair("AT", "DM"));
				params.add(new NameValuePair("MK", oid));
				params.add(new NameValuePair("SM", "0"));
				String api_url = resource.getAPIURL();
				method = new PostMethod(api_url);
				_params = new NameValuePair[params.size()];
				method.setRequestBody(params.toArray(_params));
				method.setRequestHeader("Accept-Language", "zh-cn");
				method.setRequestHeader("Accept-Encoding", " gzip, deflate");
				method.setRequestHeader("If-Modified-Since", "Thu, 29 Jul 2004 02:24:49 GMT");
				method.setRequestHeader("If-None-Match", "'3014d-1d31-41085ff1'");
				method.setRequestHeader("User-Agent", " Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.2)");
				method.setRequestHeader("Host", resource.getSiteURL());
				method.setRequestHeader("Connection", " Keep-Alive");
				method.setRequestHeader("Cookie", cookieStr);
				status = client.executeMethod(method);
				String result = method.getResponseBodyAsString();
				if (!StringUtil.isEmptyStr(result) && result.contains("<script language='javascript'>")) {
					logger.info(result);
					result = result.substring(result.lastIndexOf("<script language='javascript'>"), result.lastIndexOf("</script>"));
					String redirect_url = result.substring(result.indexOf("url = \"") + ("url = ".length()) + 1, result.indexOf("\";"));
					redirect_url = redirect_url.replace("\\x3a", ":").replace("\\x2f", "/").replace("\\x3f", "?").replace("\\x3d", "=").replace("\\x26", "&").replace("\\x25", "%");
					logger.info(URLDecoder.decode(redirect_url, "UTF-8"));
					method = new PostMethod(URLDecoder.decode(redirect_url, "UTF-8"));
					status = client.executeMethod(method);
				}
				Header header = method.getResponseHeader("location");
				if ((status == HttpStatus.SC_MOVED_TEMPORARILY) || (status == HttpStatus.SC_MOVED_PERMANENTLY)
						|| (status == HttpStatus.SC_SEE_OTHER) || (status == HttpStatus.SC_TEMPORARY_REDIRECT)) {
					// 获取重定向后的URL地址
					if (header != null) {
						String newURI = header.getValue();
						// 新的URL没有带协议和IP地址信息
						logger.debug("预定结果返回：" + newURI);
						result = newURI.substring(newURI.lastIndexOf("=") + 1);
					}
				} else if (status == HttpStatus.SC_OK) {
					String uri = method.getURI().getURI();
					if (uri.indexOf("MK") != -1) {
						result = uri.substring(uri.lastIndexOf("MK=") + 3);
					}
					String info = "";
					if (uri.contains("ST=")) {
						info = uri.substring(uri.lastIndexOf("ST=") + 3);
						info = info.substring(0, info.indexOf("&"));
						info += "----->" + result;
					}
					if (uri.contains("RS=")) {
						info += ":" + uri.substring(uri.lastIndexOf("RS=") + 3);
					}
					logger.info("--WebEx会议--" + info);
				}
				if (!StringUtil.isEmptyStr(result)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("--删除WebEx会议[URL API接口]失败!", e);
		}
		return false;
	}

	/**************************************************************************************************************************/
	/******************************************************* WebEx XML API 接口 *************************************************/
	/**************************************************************************************************************************/
	/**
	 * <pre>
	 * 描述:主持WebEx[XML API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月5日 上午10:33:26
	 * 参数：(参数列表)
	 * @param resource	资源对象
	 * @param key		WebEx会议唯一标示
	 * @return
	 * </pre>
	 */
	public static String holdXMLWebEx(String key, WebexResource resource) {
		try {
			String url = resource.getSiteURL() + "/WBXService/XMLService";
			String xml = "";
			xml = "<?xml version='1.0' encoding='UTF-8'?>\r\n";
			xml += "<serv:message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
			xml += " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\"";
			xml += " xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">\r\n";
			xml += "	<header>\r\n";
			xml += "		  <securityContext>\r\n";
			xml += "			<siteName>" + url.substring(url.indexOf("://") + "://".length(), url.indexOf(".webex")) + "</siteName>\r\n";
			xml += "			<webExID>" + resource.getAccount() + "</webExID>\r\n";
			xml += "			<password>" + resource.getPassword() + "</password>\r\n";
			xml += "		  </securityContext>\r\n";
			xml += "	</header>\r\n";
			xml += "	<body>\r\n";
			xml += "		<bodyContent xsi:type='java:com.webex.service.binding.meeting.GethosturlMeeting'>\r\n";
			xml += "			<meetingKey>" + key + "</meetingKey>\r\n";// 注意大小写
			xml += "		</bodyContent>\r\n";
			xml += "	</body>\r\n";
			xml += "</serv:message>\r\n";
			// xml请求
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			// send request
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.println(xml);
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			String result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			// output response
			result = URLDecoder.decode(result, "UTF-8");
			logger.info(url + "\r\n" + xml);
			logger.info(resultWebEx(result));
			if (!StringUtil.isEmptyStr(result)) {
				JSONObject json = resultWebEx(result);
				String status = json.containsKey("result") ? json.getString("result") : null;
				if (!StringUtil.isEmptyStr(status) && status.equalsIgnoreCase("SUCCESS")) {
					String host_url = "";
					if (json.containsKey("hostMeetingURL")) {
						host_url = json.getString("hostMeetingURL");
						host_url = host_url.replace("&amp;", "&");
						return host_url;
					}
					logger.info("--WebEx会议信息{会议号:" + key + ",主持人URL:" + host_url + "}");
				} else {
					String info = "";
					if (json.containsKey("reason")) {
						info = json.getString("reason");
					}
					logger.warn("--WebEx API访问失败,原因:" + info);
				}
			} else {
				logger.warn("--主持WebEx[XML API接口]无结果!");
			}
		} catch (Exception e) {
			logger.error("--主持WebEx[XML API接口]失败!", e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 描述:加入WebEx[XML API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月5日 上午10:42:52
	 * 参数：(参数列表)
	 * @param resource	资源对象
	 * @param email		参与人邮箱
	 * @param userName	参会人姓名
	 * @param key		WebEx会议唯一标示
	 * @return
	 * </pre>
	 */
	public static String joinXMLWebEx(String key, WebexResource resource) {
		try {
			String url = resource.getSiteURL() + "/WBXService/XMLService";
			String xml = "";
			xml = "<?xml version='1.0' encoding='UTF-8'?>\r\n";
			xml += "<serv:message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
			xml += " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\"";
			xml += " xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">\r\n";
			xml += "	<header>\r\n";
			xml += "		  <securityContext>\r\n";
			xml += "			<siteName>" + url.substring(url.indexOf("://") + "://".length(), url.indexOf(".webex")) + "</siteName>\r\n";
			xml += "			<webExID>" + resource.getAccount() + "</webExID>\r\n";
			xml += "			<password>" + resource.getPassword() + "</password>\r\n";
			xml += "		  </securityContext>\r\n";
			xml += "	</header>\r\n";
			xml += "	<body>\r\n";
			xml += "		<bodyContent xsi:type='java:com.webex.service.binding.meeting.GetjoinurlMeeting'>\r\n";
			xml += "			<meetingKey>" + key + "</meetingKey>\r\n";// 注意大小写
			xml += "		</bodyContent>\r\n";
			xml += "	</body>\r\n";
			xml += "</serv:message>\r\n";
			// xml请求
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			// send request
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.println(xml);
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			String result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			// output response
			result = URLDecoder.decode(result, "UTF-8");
			logger.info(url + "\r\n" + xml);
			logger.info(resultWebEx(result));
			if (!StringUtil.isEmptyStr(result)) {
				JSONObject json = resultWebEx(result);
				String status = json.containsKey("result") ? json.getString("result") : null;
				if (!StringUtil.isEmptyStr(status) && status.equalsIgnoreCase("SUCCESS")) {
					String join_url = "";
					if (json.containsKey("joinMeetingURL")) {
						join_url = json.getString("joinMeetingURL");
						join_url = join_url.replace("&amp;", "&");
						return join_url;
					}
					logger.info("--WebEx会议信息{会议号:" + key + ",参会人URL:" + join_url + "}");
				} else {
					String info = "";
					if (json.containsKey("reason")) {
						info = json.getString("reason");
					}
					logger.warn("--WebEx API访问失败,原因:" + info);
				}
			} else {
				logger.warn("--加入WebEx[XML API接口]无结果!");
			}
		} catch (Exception e) {
			logger.error("--加入WebEx[XML API接口]失败!", e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 描述:同步WebEx会议[XML API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月12日 上午11:29:43
	 * 参数：(参数列表)
	 * @param meeting	会议内容
	 * @param resource	资源对象
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static String synXMLWebEx(Meeting meeting, WebexResource resource) throws Exception {
		String key = meeting.getOid();
		try {
			String url = resource.getSiteURL() + "/WBXService/XMLService";
			String xml = "";
			xml = "<?xml version='1.0' encoding='UTF-8'?>\r\n";
			xml += "<serv:message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
			xml += " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\"";
			xml += " xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">\r\n";
			xml += "	<header>\r\n";
			xml += "		<securityContext>\r\n";
			xml += "			<siteName>" + url.substring(url.indexOf("://") + "://".length(), url.indexOf(".webex")) + "</siteName>\r\n";
			xml += "			<webExID>" + resource.getAccount() + "</webExID>\r\n";
			xml += "			<password>" + resource.getPassword() + "</password>\r\n";
			xml += "		</securityContext>\r\n";
			xml += "	</header>\r\n";
			xml += "	<body>\r\n";
			String webexType = StringUtil.isEmptyStr(meeting.getOid()) ? "java:com.webex.service.binding.meeting.CreateMeeting" : "java:com.webex.service.binding.meeting.SetMeeting";
			xml += "		<bodyContent xsi:type='" + webexType + "'>\r\n";
			if (!StringUtil.isEmptyStr(key)) {
				xml += "		<meetingkey>" + key + "</meetingkey>\r\n";// 注意大小写
			}
			xml += "		    <accessControl>\r\n";
			xml += "		        <meetingPassword>" + meeting.getOkey() + "</meetingPassword>\r\n";
			xml += "		    </accessControl>\r\n";
			xml += "		    <metaData>\r\n";
			xml += "		        <confName>" + meeting.getName() + "</confName>\r\n";// java.net.URLDecoder.decode(meeting.getName(), "utf-8") java.net.URLEncoder.encode(meeting.getName(), "utf-8")
//			xml += "		        <language>SIMPLIFIED CHINESE</language>\r\n";
			xml += "		    </metaData>\r\n";
			xml += "		    <schedule>\r\n";
			xml += "		        <startDate>" + DateUtil.formatDateTimeStr(meeting.getStartTime(), DateUtil.ISO_FORMATE_DATE_TIME) + "</startDate>\r\n";
			xml += "		        <duration>" + DateUtil.intervalMinutes(meeting.getStartTime(), meeting.getEndTime()) + "</duration>\r\n";
			xml += "		    </schedule>\r\n";
			xml += "		</bodyContent>\r\n";
			xml += "	</body>\r\n";
			xml += "</serv:message>\r\n";
			logger.info(url + "\r\n" + xml);
			// xml请求
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			out.println(xml);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			String result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			result = URLDecoder.decode(result, "UTF-8");
			logger.info(resultWebEx(result));
			if (!StringUtil.isEmptyStr(result)) {
				JSONObject json = resultWebEx(result);
				String status = json.containsKey("result") ? json.getString("result") : null;
				if (!StringUtil.isEmptyStr(status) && status.equalsIgnoreCase("SUCCESS")) {
					if (json.containsKey("meetingkey")) {
						key = json.getString("meetingkey");
					}
				} else {
					String info = "";
					if (json.containsKey("reason")) {
						info = json.getString("reason");
					}
					logger.warn("--WebEx API访问失败,原因:" + info);
				}
			}
			logger.info("--WebEx会议唯一标示:" + key);
		} catch (Exception e) {
			logger.error("--同步WebEx会议[XML API接口]失败!", e);
			key = "";
		}
		return key;
	}

	/**
	 * <pre>
	 * 描述:查询WebEx会议详情[XML API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月12日 上午11:29:43
	 * 参数：(参数列表)
	 * @param key		WebEx唯一标示
	 * @param resource	资源对象
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static String findXMLWebEx(String key, WebexResource resource) throws Exception {
		try {
			String url = resource.getSiteURL() + "/WBXService/XMLService";
			String xml = "";
			xml = "<?xml version='1.0' encoding='UTF-8'?>\r\n";
			xml += "<serv:message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
			xml += " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\"";
			xml += " xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">\r\n";
			xml += "	<header>\r\n";
			xml += "		  <securityContext>\r\n";
			xml += "			<siteName>" + url.substring(url.indexOf("://") + "://".length(), url.indexOf(".webex")) + "</siteName>\r\n";
			xml += "			<webExID>" + resource.getAccount() + "</webExID>\r\n";
			xml += "			<password>" + resource.getPassword() + "</password>\r\n";
			xml += "		  </securityContext>\r\n";
			xml += "	</header>\r\n";
			xml += "	<body>\r\n";
			xml += "		<bodyContent xsi:type='java:com.webex.service.binding.meeting.GetMeeting'>\r\n";
			xml += "			<meetingKey>" + key + "</meetingKey>\r\n";// 注意大小写
			xml += "		</bodyContent>\r\n";
			xml += "	</body>\r\n";
			xml += "</serv:message>\r\n";
			// xml请求
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			// send request
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.println(xml);
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			String result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			// output response
			result = URLDecoder.decode(result, "UTF-8");
			logger.info(url + "\r\n" + xml);
			logger.info(resultWebEx(result));
			if (!StringUtil.isEmptyStr(result)) {
				JSONObject json = resultWebEx(result);
				String status = json.containsKey("result") ? json.getString("result") : null;
				if (!StringUtil.isEmptyStr(status) && status.equalsIgnoreCase("SUCCESS")) {
					String name = "";
					String _status = "";
					String flag = "";
					if (json.containsKey("metaData") && json.getJSONObject("metaData") != null && json.getJSONObject("metaData").containsKey("confName")) {
						name = json.getJSONObject("metaData").getString("confName");
					}
					if (json.containsKey("status")) {
						_status = json.getString("status");
					}
					if (json.containsKey("hostJoined")) {
						flag = json.getString("hostJoined");
					}
					logger.info("--WebEx会议信息{会议号:" + key + ",名称:" + name + ",状态:" + _status + ",在线状态:" + flag + "}");
				} else {
					String info = "";
					if (json.containsKey("reason")) {
						info = json.getString("reason");
					}
					logger.warn("--WebEx API访问失败,原因:" + info);
				}
			} else {
				logger.warn("--WebEx API查询详情无结果!");
			}
		} catch (Exception e) {
			logger.error("--查询WebEx会议详情[XML API接口]失败!", e);
		}
		return key;
	}

	/**
	 * <pre>
	 * 描述:查询WebEx会议列表[XML API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月22日 上午10:57:24
	 * 参数：(参数列表)
	 * @param resource	资源对象
	 * @param start		开始时间
	 * @param end		结束时间
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static JSONArray findXMLWebExs(String key, WebexResource resource, Date start, Date end) throws Exception {
		try {
			String url = resource.getSiteURL() + "/WBXService/XMLService";
			String xml = "";
			xml = "<?xml version='1.0' encoding='UTF-8'?>\r\n";
			xml += "<serv:message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
			xml += " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\"";
			xml += " xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">\r\n";
			xml += "	<header>\r\n";
			xml += "		<securityContext>\r\n";
			xml += "			<siteName>" + url.substring(url.indexOf("://") + "://".length(), url.indexOf(".webex")) + "</siteName>\r\n";
			xml += "			<webExID>" + resource.getAccount() + "</webExID>\r\n";
			xml += "			<password>" + resource.getPassword() + "</password>\r\n";
			xml += "		</securityContext>\r\n";
			xml += "	</header>\r\n";
			xml += "	<body>\r\n";
			xml += "		<bodyContent xsi:type='java:com.webex.service.binding.meeting.LstsummaryMeeting'>\r\n";
			xml += "		  	<listControl>\r\n";
			xml += "				<startFrom>1</startFrom>\r\n";
			xml += "				<maximumNum>1000</maximumNum>\r\n";
			xml += "				<listMethod>OR</listMethod>\r\n";
			xml += "		  	</listControl>\r\n";
			xml += "		  	<dateScope>\r\n";
			xml += "				<startDateStart>" + DateUtil.formatDateTimeStr(start, DateUtil.ISO_FORMATE_DATE) + " 00:00:00" + "</startDateStart>\r\n";
			xml += "				<startDateEnd>" + DateUtil.formatDateTimeStr(end, DateUtil.ISO_FORMATE_DATE) + " 23:59:59" + "</startDateEnd>\r\n";
			xml += "				<endDateStart>" + DateUtil.formatDateTimeStr(start, DateUtil.ISO_FORMATE_DATE) + " 00:00:00" + "</endDateStart>\r\n";
			xml += "				<endDateEnd>" + DateUtil.formatDateTimeStr(end, DateUtil.ISO_FORMATE_DATE) + " 23:59:59" + "</endDateEnd>\r\n";
			xml += "		  	</dateScope>\r\n";
			xml += "		  	<order>\r\n";
			xml += "				<orderBy>STARTTIME</orderBy>\r\n";
			xml += "				<orderAD>ASC</orderAD>\r\n";
			xml += "		  	</order>\r\n";
			xml += "		</bodyContent>\r\n";
			xml += "	</body>\r\n";
			xml += "</serv:message>\r\n";
			// xml请求
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			// send request
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.println(xml);
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			String result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			// output response
			result = URLDecoder.decode(result, "UTF-8");
			logger.info(url + "\r\n" + xml);
			logger.info(resultWebEx(result));
			if (!StringUtil.isEmptyStr(result)) {
				JSONObject json = resultWebEx(result);
				String status = json.containsKey("result") ? json.getString("result") : null;
				if (!StringUtil.isEmptyStr(status) && status.equalsIgnoreCase("SUCCESS")) {
					JSONArray list = null;
					if (json.containsKey("meeting")) {
						if (json.get("meeting") instanceof JSONObject) {
							list = new JSONArray();
							list.add(json.getJSONObject("meeting"));
						} else {
							list = json.getJSONArray("meeting");
						}
					}
					if (list != null && list.size() > 0) {
						JSONArray meetings = new JSONArray();
						for (Object obj : list) {
							JSONObject meeting = (JSONObject) obj;
							String wid = (meeting != null && meeting.containsKey("hostWebExID") ? meeting.getString("hostWebExID") : null);
							if (meeting == null || !meeting.containsKey("startDate") || !meeting.containsKey("duration") || (!StringUtil.isEmptyStr(wid) && !wid.equals(resource.getAccount()))) {
								continue;
							}
							Date startTime = DateUtil.formatDateTime(meeting.getString("startDate"), DateUtil.ISO_FORMATE_DATE_TIME);
							int duration = meeting.getIntValue("duration");
							Date endTime = DateUtil.handleDateTimeByMinute(startTime, duration);
							if (startTime.after(end) || endTime.before(start)) {
								continue;
							}
							String name = "";
							String _status = "";
							String flag = "";
							String _key = "";
							if (meeting.containsKey("meetingKey")) {
								_key = meeting.getString("meetingKey");
								if (!StringUtil.isEmptyStr(key) && key.equals(_key)) {
									continue;
								}
							}
							if (meeting.containsKey("confName")) {
								name = meeting.getString("confName");
							}
							if (meeting.containsKey("status")) {
								_status = meeting.getString("status");
							}
							if (meeting.containsKey("hostJoined")) {
								flag = meeting.getString("hostJoined");
							}
							meetings.add(meeting);
							logger.info("--WebEx会议信息{会议号:" + _key + ",名称:" + name + ",开始时间:" + DateUtil.formatDateHMTimeStr(startTime) + ",结束时间:" + DateUtil.formatDateHMTimeStr(endTime) + ",状态:" + _status + ",在线状态:" + flag + "}");
						}
						return meetings;
					}
					logger.info("--WebEx会议[" + DateUtil.formatRange(start, end) + "]总数:" + list != null ? list.size() : 0);
				} else {
					String info = "";
					if (json.containsKey("reason")) {
						info = json.getString("reason");
					}
					logger.warn("--WebEx API访问失败,原因:" + info);
				}
			} else {
				logger.warn("--WebEx API查询详情无结果!");
			}
		} catch (Exception e) {
			logger.error("--查询WebEx会议详情[XML API接口]失败!", e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 描述:删除WebEx会议[XML API接口]
	 * 作者:ZhangYi
	 * 时间:2016年7月12日 上午11:29:43
	 * 参数：(参数列表)
	 * @param key		WebEx唯一标示
	 * @param resource	资源对象
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	public static boolean deleteXMLWebEx(String key, WebexResource resource) throws Exception {
		try {
			String url = resource.getSiteURL() + "/WBXService/XMLService";
			String xml = "";
			xml = "<?xml version='1.0' encoding='UTF-8'?>\r\n";
			xml += "<serv:message xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
			xml += " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\"";
			xml += " xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">\r\n";
			xml += "	<header>\r\n";
			xml += "		  <securityContext>\r\n";
			xml += "			<siteName>" + url.substring(url.indexOf("://") + "://".length(), url.indexOf(".webex")) + "</siteName>\r\n";
			xml += "			<webExID>" + resource.getAccount() + "</webExID>\r\n";
			xml += "			<password>" + resource.getPassword() + "</password>\r\n";
			xml += "		  </securityContext>\r\n";
			xml += "	</header>\r\n";
			xml += "	<body>\r\n";
			xml += "		<bodyContent xsi:type='java:com.webex.service.binding.meeting.DelMeeting'>\r\n";
			xml += "			<meetingKey>" + key + "</meetingKey>\r\n";// 注意大小写
			xml += "		</bodyContent>\r\n";
			xml += "	</body>\r\n";
			xml += "</serv:message>\r\n";
			// xml请求
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			// send request
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.println(xml);
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			String result = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			// output response
			result = URLDecoder.decode(result, "UTF-8");
			logger.info(url + "\r\n" + xml);
			logger.info(resultWebEx(result));
			if (!StringUtil.isEmptyStr(result)) {
				JSONObject json = resultWebEx(result);
				String status = json.containsKey("result") ? json.getString("result") : null;
				if (!StringUtil.isEmptyStr(status) && status.equalsIgnoreCase("SUCCESS")) {
					return true;
				} else {
					String info = "";
					if (json.containsKey("reason")) {
						info = json.getString("reason");
					}
					logger.warn("--WebEx API访问失败,原因:" + info);
				}
			}
			logger.info("--WebEx会议唯一标示:" + key);
		} catch (Exception e) {
			logger.error("--删除WebEx会议[XML API接口]失败!", e);
		}
		return false;
	}

	/**
	 * <pre>
	 * 描述:WebEx结果XML转JSON
	 * 作者:ZhangYi
	 * 时间:2016年7月21日 下午6:18:16
	 * 参数：(参数列表)
	 * @param xml	xml字符串
	 * @return
	 * </pre>
	 */
	private static JSONObject resultWebEx(String xml) {
		JSONObject json = new JSONObject();
		JSONObject data = XMLUtil.xmlToJSON(xml);
		if (data != null && data.containsKey("message")) {
			data = data.getJSONObject("message");
			if (data.containsKey("header") && data.getJSONObject("header") != null && data.getJSONObject("header").containsKey("response")) {
				JSONObject response = data.getJSONObject("header").getJSONObject("response");
				if (response.containsKey("result")) {
					json.put("result", response.getString("result"));
				}
				if (response.containsKey("gsbStatus")) {
					json.put("status", response.getString("gsbStatus"));
				}
				if (response.containsKey("reason")) {
					json.put("reason", response.getString("reason"));
				}
			}
			if (data.containsKey("body") && data.getJSONObject("body") != null && data.getJSONObject("body").containsKey("bodyContent")) {
				JSONObject obj = data.getJSONObject("body").getJSONObject("bodyContent");
				if (obj != null) {
					json.putAll(obj);
				}
			}
		}
		return json;
	}

	public static void main(String[] args) {
		try {
			WebexResource resource = new WebexResource();
			resource.setSiteURL("https://beboldmc8.webex.com.cn");
			resource.setLoginURL("https://beboldmc8.webex.com.cn/beboldmc8/p.php");
			resource.setAPIURL("https://beboldmc8.webex.com.cn/beboldmc8/m.php");
			resource.setAccount("lisong@dev-share.com");
			resource.setPassword("P@ss1234");
			resource.setSiteURL("https://dev-share.webex.com.cn");
			resource.setLoginURL("https://dev-share.webex.com.cn/dev-share/p.php");
			resource.setAPIURL("https://dev-share.webex.com.cn/dev-share/m.php");
			resource.setAccount("lisong@dev-share.com");
			resource.setPassword("W@ferbeX123");
			System.out.println("-------------WebEx XML API[开始]---------------");
			Date start = DateUtil.formatDateTime("2016-07-19 20:50:00");
			Date end = DateUtil.formatDateTime("2016-07-19 21:00:00");
			Meeting meeting = new Meeting();
			String name = "WebEx XML API会议接口";
			meeting.setName(name);
			String key = "185848810";
			meeting.setOid(key);
			meeting.setOkey("wafer");
			meeting.setStartTime(start);
			meeting.setEndTime(end);
//			findAllWebEx(resource);
//			System.out.println(synXMLWebEx(meeting, resource));
//			findXMLWebExs(resource, start, end);
//			System.out.println(findXMLWebEx(key, resource));
//			System.out.println(holdXMLWebEx(key, resource));
//			System.out.println(joinXMLWebEx(key, resource));
//			System.out.println(deleteXMLWebEx(meeting.getOid(), resource));
			System.out.println("-------------WebEx XML API[结束]---------------");
		} catch (Exception e) {
			e.printStackTrace();// 异常信息
		}
	}
}
