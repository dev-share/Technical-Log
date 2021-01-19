package com.dev-share.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * <pre>
 * 描述:邮件工具类
 * 作者:ZhangYi
 * 时间:2015年5月28日 下午2:58:37
 * JDK:1.7.76
 * </pre>
 */
public class MailUtils {

	private static final Logger	logger			= Logger.getLogger(MailUtils.class);

	private static String		EMAIL_SMTP		= "smtp.263xmail.com";
	private static String		EMAIL_POP		= "pop.263xmail.com";
	private static String		EMAIL_FROM		= "office_helper2@dev-share.com";
	private static String		EMAIL_ACCOUNT	= "office_helper2@dev-share.com";
	private static String		EMAIL_PASSWORD	= "wAfer@help0606";

	/**
	 * <pre>
	 * 描述:邮件发送
	 * 作者:ZhangYi
	 * 时间:2016年6月29日 下午2:56:56
	 * 参数：(参数列表)
	 * @param sender 	发送人名称
	 * @param mails 	收件人(多邮件以;分割)
	 * @param title 	邮件主题
	 * @param content 	邮件内容
	 * @param status	邮件状态(-1.纯文本信息,0.邮件事件[带邀请按钮],1.邮件事件[无按钮])
	 * @param startTime	事件开始时间
	 * @param endTime	事件结束时间
	 * @param location	地点
	 * @throws Exception
	 * </pre>
	 */
	public static void sendMail(String sender, String mails, String title, String content, int status, Date startTime, Date endTime, String location) {
		try {
			if (StringUtil.isEmptyStr(mails)) {
				logger.info("-------主题为:[" + title + "]收件人邮箱不能为空----------");
				return;
			}
			String to = "";
			String[] emails = mails.split(";");
			if (emails != null && emails.length > 0) {
				for (String email : emails) {
					if (!StringUtil.isEmptyStr(email)) {
						if (email.contains("@") && email.contains(".")) {
							boolean flag = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
									.matcher(email).matches();
							if (flag) {
								if (!StringUtil.isEmptyStr(to)) {
									to += "," + email;
								} else {
									to = email;
								}
							} else {
								logger.info("-------主题为:[" + title + "]收件人邮箱[" + email + "]格式不正确----------");
							}
						} else {
							logger.info("-------主题为:[" + title + "]收件人邮箱[" + email + "]格式不正确----------");
						}
					}
				}
			}
			// 邮件正文
			Multipart multipart = new MimeMultipart();
			if (status != -1) {
				String method = "";
				if (status == 0) {
					method = "REQUEST";
				} else {
					method = "PUBLISH";
				}
				String calendar = "";
				String start = DateUtil.formatDateTimeStr(startTime, "yyyyMMdd'T'HHmmss");
				String end = DateUtil.formatDateTimeStr(endTime, "yyyyMMdd'T'HHmmss");
				String uid = UUID.randomUUID().toString();
				String valarm = "";
				valarm += "BEGIN:VALARM\n"
						+ "TRIGGER:-PT10M\n"
						+ "REPEAT:3\n"
						+ "DURATION:PT5M\n"
						+ "ACTION:DISPLAY\n"
						+ "DESCRIPTION:Reminder\n"
						+ "END:VALARM\n";
				String vevent = "";
				vevent += "BEGIN:VEVENT\n"
						+ "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:" + mails + "\n" + "ORGANIZER:MAILTO:" + EMAIL_FROM + "\n"
						+ "DTSTART:" + start + "Z\n"
						+ "DTEND:" + end + "Z\n"
						+ "LOCATION:" + location + "\n"
						+ "UID:" + uid + "\n"
						+ "SEQUENCE:" + 5 + "\n"
						+ "CATEGORIES:SuccessCentral Reminder\n"
						+ "DESCRIPTION:" + "\n"
						+ "SUMMARY:" + title + "\n"
						+ "PRIORITY:5\n"
						+ "CLASS:PUBLIC\n"
						+ valarm
						+ "END:VEVENT\n";
				calendar = "BEGIN:VCALENDAR\n" + "PRODID:-//Events Calendar//iCal4j 1.0//EN\n" + "VERSION:2.0\n" + "METHOD:" + method + "\n" + vevent + "END:VCALENDAR";
				// 第一个为事件内容
				MimeBodyPart event = new MimeBodyPart();
				event.setDataHandler(new DataHandler(new ByteArrayDataSource(calendar, "text/calendar;method=" + method + ";charset=UTF-8")));
				multipart.addBodyPart(event);
			}
			Properties props = System.getProperties();
			props.put("mail.smtp.host", EMAIL_SMTP);//
			// 设置邮件的字符集为GBK
			props.put("mail.mime.charset", "GBK");
			// 设置认证模式
			props.put("mail.smtp.auth", "true");
			// 获取会话信息
			Session session = Session.getDefaultInstance(props, null);
			// 构造邮件消息对象
			MimeMessage message = new MimeMessage(session);
			// 发件人
			message.setFrom(new InternetAddress(EMAIL_FROM, sender));
			// 多个发送地址
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			// 邮件主题
			message.setSubject(title);
			// 第一个为文本内容。
			BodyPart body = new MimeBodyPart();
			body.setContent(content, "text/html;charset=UTF-8");
			multipart.addBodyPart(body);
			// 使用多个body体填充邮件内容。
			message.setContent(multipart);
			// 免认证模式
			// Transport.send(message, message.getAllRecipients());
			// 认证模式
			Transport transport = session.getTransport("smtp");
			transport.connect(EMAIL_SMTP, EMAIL_ACCOUNT, EMAIL_PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("---向[" + mails + "]发送[" + title + "]邮件成功!----------");
		} catch (Exception e) {
			logger.error("---向[" + mails + "]发送[" + title + "]邮件失败!", e);
		}
	}

	/**
	 * <pre>
	 * 描述:邮件发送
	 * 作者:ZhangYi
	 * 时间:2016年6月29日 下午2:56:56
	 * 参数：(参数列表)
	 * @param sender 	发送人名称
	 * @param mails 	收件人(多邮件以;分割)
	 * @param title 	邮件主题
	 * @param content 	邮件内容
	 * @param status	邮件状态(-1.纯文本信息,0.邮件事件[带邀请按钮],1.邮件事件[无按钮])
	 * @param startTime	事件开始时间
	 * @param endTime	事件结束时间
	 * @param location	地点
	 * @throws Exception
	 * </pre>
	 */
	public static void sendEmail(String sender, String mails, String title, String content, int status, Date startTime, Date endTime, String location) {
		try {
			if (StringUtil.isEmptyStr(mails)) {
				logger.info("-------主题为:[" + title + "]收件人邮箱不能为空----------");
				return;
			}
			String to = "";
			String[] emails = mails.split(";");
			if (emails != null && emails.length > 0) {
				for (String email : emails) {
					if (!StringUtil.isEmptyStr(email)) {
						if (email.contains("@") && email.contains(".")) {
							boolean flag = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
									.matcher(email).matches();
							if (flag) {
								if (!StringUtil.isEmptyStr(to)) {
									to += "," + email;
								} else {
									to = email;
								}
							} else {
								logger.info("-------主题为:[" + title + "]收件人邮箱[" + email + "]格式不正确----------");
							}
						} else {
							logger.info("-------主题为:[" + title + "]收件人邮箱[" + email + "]格式不正确----------");
						}
					}
				}
			}
			Multipart multipart = new MimeMultipart();
			if (status != -1) {
				Method method = null;
				if (status == 0) {
					method = Method.REQUEST;
				} else {
					method = Method.PUBLISH;
				}
				DateTime start = new DateTime(startTime);
				DateTime end = new DateTime(endTime);
				String remark = title + "[" + DateUtil.formatRange(startTime, endTime) + "]";
				VEvent meeting = new VEvent(start, end, title);

				VAlarm valarm = new VAlarm();// 提醒
				valarm.getProperties().add(new Trigger(new DateTime(DateUtil.handleDateTimeByMinute(startTime, -10))));// 提前10分钟提醒
				valarm.getProperties().add(new Repeat(3));// 提醒三次
				valarm.getProperties().add(new Duration(startTime, endTime));
				valarm.getProperties().add(Action.DISPLAY);// 提醒窗口显示的文字信息
				valarm.getProperties().add(new Description(remark));
				meeting.getAlarms().add(valarm);// 将VAlarm加入VEvent
				for (String email : emails) {
					Attendee attendee = new Attendee("mailto:" + email);
					attendee.getParameters().add(Role.REQ_PARTICIPANT);
					attendee.getParameters().add(Rsvp.TRUE);
					attendee.getParameters().add(new Cn(email));
					meeting.getProperties().add(attendee);
				}
				meeting.getProperties().add(new Organizer("mailto:" + EMAIL_FROM));
				meeting.getProperties().add(new Location(location));
				meeting.getProperties().add(new Uid(UUID.randomUUID().toString()));
				meeting.getProperties().add(new Sequence(5));
				meeting.getProperties().add(new Categories(remark));
				meeting.getProperties().add(new Summary(title));
				meeting.getProperties().add(new Description(remark));
				meeting.getProperties().add(Priority.MEDIUM);
				meeting.getProperties().add(Clazz.PUBLIC);

				Calendar calendar = new Calendar();
				calendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
				calendar.getProperties().add(Version.VERSION_2_0);
				calendar.getProperties().add(method);
				calendar.getComponents().add(meeting);

				CalendarOutputter ocalendar = new CalendarOutputter(false);
				Writer wcalendar = new StringWriter();
				ocalendar.output(calendar, wcalendar);
				// 第一个为事件内容
				MimeBodyPart event = new MimeBodyPart();
				event.setDataHandler(new DataHandler(new ByteArrayDataSource(wcalendar.toString(), "text/calendar;method=" + method.getValue() + ";charset=UTF-8")));
				multipart.addBodyPart(event);
			}
			Properties props = System.getProperties();
			props.put("mail.smtp.host", EMAIL_SMTP);//
			// 设置邮件的字符集为GBK
			props.put("mail.mime.charset", "GBK");
			// 设置认证模式
			props.put("mail.smtp.auth", "true");
			// 获取会话信息
			Session session = Session.getDefaultInstance(props, null);
			// 构造邮件消息对象
			MimeMessage message = new MimeMessage(session);
			// 发件人
			message.setFrom(new InternetAddress(EMAIL_FROM, sender));
			// 多个发送地址
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			// 邮件主题
			message.setSubject(title);
			// 第一个为文本内容。
			BodyPart body = new MimeBodyPart();
			body.setContent(content, "text/html;charset=UTF-8");
			multipart.addBodyPart(body);
			// 使用多个body体填充邮件内容。
			message.setContent(multipart);
			// 免认证模式
			// Transport.send(message, message.getAllRecipients());
			// 认证模式
			Transport transport = session.getTransport("smtp");
			transport.connect(EMAIL_SMTP, EMAIL_ACCOUNT, EMAIL_PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("---向[" + mails + "]发送[" + title + "]邮件成功!----------");
		} catch (Exception e) {
			logger.error("---向[" + mails + "]发送[" + title + "]邮件失败!", e);
		}
	}

	/**
	 * <pre>
	 * 描述:邮件回执接收
	 * 作者:ZhangYi
	 * 时间:2016年6月29日 下午5:23:02
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 */
	public static String receiveMail() {
		String result = "";
		Properties properties = new Properties();
		properties.put("mail.store.protocol", "pop3");
		properties.put("mail.pop3.host", EMAIL_POP);
		properties.put("mail.pop3.user", EMAIL_ACCOUNT);
		Session mailsession = Session.getDefaultInstance(properties, null);
		mailsession.setDebug(false);
		try {
			Store store = mailsession.getStore("pop3");
			store.connect(EMAIL_ACCOUNT, EMAIL_PASSWORD);
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			System.out.println("-------------------------------------------------------------");
			Message[] msgs = folder.getMessages();
			for (Message message : msgs) {
				String title = message.getSubject();
//				String member = message.getHeader("X-SENDER")[0];//X-RL-SENDER
				if (title.contains("Accept") || title.contains("接受")) {

				} else {
					if (title.contains("Refuse") || title.contains("谢绝")) {

					} else {

					}
				}
				Enumeration enums = message.getAllHeaders();
				System.out.println(JSON.toJSONString(enums));
//				message.setFlag(Flags.Flag.DELETED, true);
				System.out.println(title);
			}
			folder.close(true);
			store.close();
		} catch (Exception e) {
			logger.error("邮件读取时发生错误", e);
		}
		return result;
	}

	/**
	 * 描述：main方法 作者:ZhangYi 时间:2015年5月28日 下午2:58:37 参数列表:
	 * 
	 * @param args 参数
	 */
	public static void main(String[] args) {
		String mails = "3@dev-share.com;1@dev-share.com;2@dev-share.com";
		String title = "你牛什麼牛!" + new Date().toLocaleString();
		String content = "<div style='font-weight:bolder;font-size:32px;padding:10px;'>请选择一种传播方式：&nbsp;&nbsp;<a href='http://dongtaiwang.com/loc/phome.php?v=7.42p&l=804'><font color='blue'>1.木马</font>&nbsp;&nbsp;&nbsp;<font color='purple'>2.蠕虫</font>&nbsp;&nbsp;&nbsp;<font color='green'>3.病毒</font></a></div>";
		content += "<hr/><hr/><font color='red' style='font-size:15px;'>温馨提示：由于你的电脑已被感染,如果你不配合传播,你的电脑关机后将无法启动!</font>";
//		content += "<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>";
		content += "<link rel='stylesheet' href='http://apps.bdimg.com/libs/bootstrap/3.3.4/css/bootstrap.css' type='text/css' />";
		content += "<hr/><font color='red' style='font-size:20px;'>开个玩笑别介意!</font>";
		content += "<hr/><a class='btn btn-danger' href='www.baidu.com'>同意</a>";
		try {
			System.out.println("-----------------邮件发送[开始]-----------------");
			sendMail("dev-share", mails, title + "[REQUEST]", content, 0, DateUtil.formatDateTime("2016-07-01 12:00"), DateUtil.formatDateTime("2016-07-01 13:00"), "美国国情局");
			sendMail("dev-share", mails, title + "[PUBLISH]", content, 1, DateUtil.formatDateTime("2016-07-01 12:00"), DateUtil.formatDateTime("2016-07-01 13:00"), "美国国情局");
			sendMail("dev-share", mails, title + "[Text]", content, -1, null, null, null);
			sendEmail("dev-share", mails, title + "[E_REQUEST]", content, 0, DateUtil.formatDateTime("2016-07-01 13:00"), DateUtil.formatDateTime("2016-07-01 14:00"), "美国国防部");
			sendEmail("dev-share", mails, title + "[E_PUBLISH]", content, 1, DateUtil.formatDateTime("2016-07-01 13:00"), DateUtil.formatDateTime("2016-07-01 14:00"), "美国国防部");
			sendEmail("dev-share", mails, title + "[E_Text]", content, -1, null, null, null);
			System.out.println("-----------------邮件发送[结束]-----------------");
			System.out.println("-----------------邮件读取[开始]-----------------");
//			receiveMail();
			System.out.println("-----------------邮件读取[结束]-----------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
