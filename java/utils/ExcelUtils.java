package com.dev-share.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.BooleanFormulaCell;
import jxl.Cell;
import jxl.CellType;
import jxl.DateFormulaCell;
import jxl.NumberFormulaCell;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.Pattern;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * <pre>
 * 项目:公共类
 * 描述:Excel上传下载工具
 * 说明:JXL/POI技术实现
 * 作者:ZhangYi
 * 时间:2016年11月14日 下午4:11:29
 * 版本:wsm_v3.5
 * JDK:1.7.80
 * </pre>
 */
@SuppressWarnings("all")
public class ExcelUtils {
	/**
	 * <pre>
	 * 描述:Excel导出
	 * 说明:(JXL实现)
	 * 作者:ZhangYi
	 * 时间:2016年9月6日 下午3:07:55
	 * 参数：(参数列表)
	 * @param title		标题
	 * @param header	列标题
	 * @param rows		行数据
	 * @param response	响应请求
	 * @return
	 * </pre>
	 */
	public static boolean downloadExcel(String title, String[] header, List<String[]> rows,HttpServletResponse response) {
		try {
			OutputStream out = response.getOutputStream();
			response.reset();
			response.setHeader("Content-Disposition","inline;filename=" + new String(title.getBytes("GBK"), "ISO8859_1")+ DateUtil.formatDateTimeStr(new Date(), "yyyyMMddhhmmss") + ".xls");
			response.setContentType("application/octet-stream");
			WritableWorkbook book = Workbook.createWorkbook(out);
			WritableSheet sheet = book.createSheet(title, 0);
			/** **********设置纵横打印（默认为纵打）、打印纸***************** */
			SheetSettings sheetset = sheet.getSettings();
			sheetset.setProtected(false);
			/******************************************************************************/
			// 1.通用样式字体设置
			WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
			WritableCellFormat style = new WritableCellFormat(font);
			style.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
			style.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
			style.setAlignment(Alignment.CENTRE); // 文字水平对齐
			style.setWrap(false); // 文字是否换行
			// 2.(1)主题样式字体设置
			WritableFont tfont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
			tfont.setItalic(false);
			tfont.setUnderlineStyle(UnderlineStyle.NO_UNDERLINE);
			tfont.setColour(Colour.WHITE);
			WritableCellFormat tstyle = new WritableCellFormat(tfont);
			tstyle.setBorder(Border.NONE, BorderLineStyle.THIN); // 线条
			tstyle.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
			tstyle.setAlignment(Alignment.CENTRE); // 文字水平对齐
			tstyle.setBackground(Colour.DARK_BLUE, Pattern.SOLID);
			tstyle.setWrap(false); // 文字是否换行
			// 2.(2)列标题样式字体设置
			WritableCellFormat hstyle = new WritableCellFormat(tfont);
			hstyle.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
			hstyle.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
			hstyle.setAlignment(Alignment.CENTRE); // 文字水平对齐
			hstyle.setBackground(Colour.GREEN, Pattern.SOLID);
			hstyle.setWrap(false); // 文字是否换行
			/******************************************************************************/
			sheet.mergeCells(0, 0, header.length - 1, 0);// 合并列
			sheet.setRowView(0, 51 * 20);// 行基数:20
			sheet.addCell(new Label(0, 0, title, tstyle));// 主题
			sheet.setRowView(1, 30 * 20);// 行基数:20
			for (int i = 0; i < header.length; i++) {
				sheet.setColumnView(i, header.length > 10 ? (header.length > 20 ? 10 : 17) : 22);
				String value = header[i];
				sheet.addCell(new Label(i, 1, value, hstyle));
			}
			if (rows != null && rows.size() > 0) for (int i = 0; i < rows.size(); i++) {
				sheet.setRowView(i + 2, 18 * 20);// 行基数:20
				for (int j = 0; j < header.length; j++) {
					String value = (rows.get(i))[j];
					sheet.addCell(new Label(j, i + 2, value, style));
				}
			}
			book.write();
			book.close();
			out.flush();
			out.close();
			return true;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * <pre>
	 * 描述:读取多工作表的Excel工作薄
	 * 说明:(JXL实现)Map(key为列名,value为行列值)为行对象
	 * 作者:ZhangYi
	 * 时间:2016年9月7日 下午6:47:30
	 * 参数：(参数列表)
	 * @param file	文件源
	 * @return	返回多行集合
	 * </pre>
	 */
	public static List<Map<String, Object>> readExcel(InputStream in) {
		if (in == null) return null;
		List<Map<String, Object>> list = null;
		try {
			jxl.Workbook workbook = Workbook.getWorkbook(in);
			int count = workbook.getNumberOfSheets();// 工作表数目
			if (count > 0) {
				list = new ArrayList<Map<String, Object>>();
				for (int k = 0; k < count; k++) {
					jxl.Sheet sheet = workbook.getSheet(k);// 当前工作表
					int rows = sheet.getRows();// 得到总行数
					if (rows == 0) {
						continue;
					}
					for (int i = 1; i <= rows; i++) {// 正文内容应该从第二行开始,第一行为表头的标题
						boolean rowvalue = false;//判断行是否全为空,默认空行
						Cell[] cells = sheet.getRow(i);// 行
						Map<String, Object> obj = new HashMap<String, Object>();// 相当对象
						for (int j = 0; j < cells.length; j++) {
							Cell cell = sheet.getRow(0)[j];
							if(cell == null||cell.getType() == CellType.STRING_FORMULA)continue;
							String key = cell.getContents();// 标题
							cell = cells[j];
							Object value = null;// 列值
							if(cell!=null){
								if (cell.getType() == CellType.STRING_FORMULA) {
									value = cell.getContents();
								}
								if (cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA) {
									value = ((NumberFormulaCell) cell).getValue();
								}
								if (cell.getType() == CellType.DATE || cell.getType() == CellType.DATE_FORMULA) {
									value = ((DateFormulaCell) cell).getDate();
								}
								if (cell.getType() == CellType.BOOLEAN || cell.getType() == CellType.BOOLEAN_FORMULA) {
									value = ((BooleanFormulaCell) cell).getValue();
								}
							}
							if(value!=null&&!StringUtil.isEmptyStr((String)value)){
								rowvalue = true;
							}
							obj.put(key, value);// 相当对象属性
						}
						if(rowvalue){
							list.add(obj);
						}
					}
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * <pre>
	 * 描述:Excel导出
	 * 说明:(POI实现)
	 * 作者:ZhangYi
	 * 时间:2016年9月6日 下午3:07:55
	 * 参数：(参数列表)
	 * @param title		标题
	 * @param header	列标题
	 * @param rows		行数据
	 * @param response	响应请求
	 * @return
	 * </pre>
	 */
	public static boolean downloadPExcel(String title, String[] header, List<String[]> rows,HttpServletResponse response) {
		try {
			OutputStream out = response.getOutputStream();
			response.reset();
			response.setContentType("application/octet-stream"); // MediaType.APPLICATION_OCTET_STREAM_VALUE=application/octet-stream
			response.setHeader("Content-Disposition","attachment;filename=\"" + new String(title.getBytes("GBK"), "ISO8859_1")+ DateUtil.formatDateTimeStr(new Date(), "yyyyMMddHHmmss") + ".xls\"");
			HSSFWorkbook book = new HSSFWorkbook();
			HSSFSheet sheet = book.createSheet(title);
			// sheet.autoSizeColumn(1, true);//自适应列宽度
			/******************************************************************************/
			// 1.通用样式字体设置
			HSSFFont font = book.createFont();// 字体设置
			font.setColor(HSSFColor.BLACK.index);
			font.setFontHeightInPoints((short) 10);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			HSSFCellStyle style = book.createCellStyle();// 样式设置
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平对齐:居中
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直对齐:居中
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
			style.setFont(font);
			// 2.(1)主题样式字体设置
			HSSFFont tfont = book.createFont();// 字体设置
			tfont.setColor(HSSFColor.WHITE.index);
			tfont.setFontHeightInPoints((short) 12);
			tfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 字体加粗
			HSSFCellStyle tstyle = book.createCellStyle();// 样式设置
			tstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平对齐:居中
			tstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直对齐:居中
			tstyle.setFillForegroundColor(HSSFColor.DARK_BLUE.index);// 背景色:黑蓝色
			tstyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 填充色:纯色
			tstyle.setFont(tfont);
			// 2.(2)列标题样式字体设置
			HSSFCellStyle hstyle = book.createCellStyle();// 样式设置
			hstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平对齐:居中
			hstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直对齐:居中
			hstyle.setFillForegroundColor(HSSFColor.GREEN.index);// 背景色:绿色
			hstyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 填充色:纯色
			hstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
			hstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
			hstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
			hstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
			hstyle.setFont(tfont);
			/******************************************************************************/
			// 合并单元格供标题使用(表名)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, header.length - 1));
			HSSFRow trow = sheet.createRow(0);// 第几行（从0开始）
			HSSFCell tcell = trow.createCell(0);
			trow.setHeightInPoints(51);
			tcell.setCellValue(title);
			tcell.setCellStyle(tstyle);
			HSSFRow row = sheet.createRow(1);
			row.setHeightInPoints(30);
			for (int i = 0; i < header.length; i++) {
				sheet.setColumnWidth(i, (header.length > 10 ? (header.length > 20 ? 10 : 17) : 22) * 256);// 列基数:256
				HSSFCell cell = row.createCell(i);
				String value = header[i];
				cell.setCellValue(value);
				cell.setCellStyle(hstyle);
			}
			if (rows != null && rows.size() > 0) for (int i = 0; i < rows.size(); i++) {
				row = sheet.createRow(i + 2);// index：第几行
				row.setHeightInPoints(18);
				for (int j = 0; j < header.length; j++) {
					HSSFCell cell = row.createCell(j);// 第几列：从0开始
					String value = (rows.get(i))[j];
					cell.setCellValue(value);
					cell.setCellStyle(style);
				}
			}
			book.write(out);
			out.flush();
			out.close();
			return true;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * <pre>
	 * 描述:读取多工作表的Excel工作薄
	 * 说明:(POI实现)Map(key为列名,value为行列值)为行对象
	 * 作者:ZhangYi
	 * 时间:2016年9月7日 下午6:47:30
	 * 参数：(参数列表)
	 * @param is		文件流
	 * @param version	Excel版本(2003/2007/2010+)
	 * @param sheet	工作表名称(空值为所有工作表)
	 * @return	返回多行集合
	 * </pre>
	 */
	public static List<Map<String, Object>> readExcel(InputStream in, int version, String sheet) {
		if (in == null) return null;
		List<Map<String, Object>> list = null;
		try {
			if (version < 2007) {
				HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(in));
				int count = workbook.getNumberOfSheets();// 工作表数目
				if (count > 0) {
					list = new ArrayList<Map<String, Object>>();
					for (int k = 0; k < count; k++) {
						HSSFSheet hsheet = workbook.getSheetAt(k);// 当前工作表
						if (!StringUtil.isEmptyStr(sheet) && !sheet.equalsIgnoreCase(hsheet.getSheetName())) {
							continue;
						}
						int rowNum = hsheet.getLastRowNum();// 得到总行数
						if (rowNum == 0) {
							continue;
						}
						int colNum = hsheet.getRow(0).getPhysicalNumberOfCells();
						for (int i = 1; i <= rowNum; i++) {// 正文内容应该从第二行开始,第一行为表头的标题
							boolean rowvalue = false;//判断行是否全为空,默认空行
							HSSFRow row = hsheet.getRow(i);// 行
							Map<String, Object> obj = new HashMap<String, Object>();// 相当对象
							for (int j = 0; j < colNum; j++) {
								HSSFCell cell = hsheet.getRow(0).getCell(j);
								if(cell == null||cell.getCellType() != XSSFCell.CELL_TYPE_STRING)continue;
								String key = cell.getStringCellValue();// 标题
								cell = row.getCell(j);
								Object value = "";// 列值
								if(cell!=null){
									if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
										value = cell.getStringCellValue();
									}
									if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
										value = cell.getNumericCellValue();
										if (HSSFDateUtil.isCellDateFormatted(cell)) {
											value = cell.getDateCellValue();
										}
									}
									if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
										value = cell.getBooleanCellValue();
									}
								}
								if(value!=null&&!StringUtil.isEmptyStr((String)value)){
									rowvalue = true;
								}
								obj.put(key, value);// 相当对象属性
							}
							if(rowvalue){
								list.add(obj);
							}
						}
					}
				}
			} else {
				XSSFWorkbook workbook = new XSSFWorkbook(in);
				int count = workbook.getNumberOfSheets();// 工作表数目
				if (count > 0) {
					list = new ArrayList<Map<String, Object>>();
					for (int k = 0; k < count; k++) {
						XSSFSheet hsheet = workbook.getSheetAt(k);// 当前工作表
						if (!StringUtil.isEmptyStr(sheet) && !sheet.equalsIgnoreCase(hsheet.getSheetName())) {
							continue;
						}
						int rowNum = hsheet.getLastRowNum();// 得到总行数
						if (rowNum == 0) {
							continue;
						}
						int colNum = hsheet.getRow(0).getPhysicalNumberOfCells();
						for (int i = 1; i <= rowNum; i++) {// 正文内容应该从第二行开始,第一行为表头的标题
							boolean rowvalue = false;//判断行是否全为空,默认空行
							XSSFRow row = hsheet.getRow(i);// 行
							Map<String, Object> obj = new HashMap<String, Object>();// 相当对象
							for (int j = 0; j < colNum; j++) {
								XSSFCell cell = hsheet.getRow(0).getCell(j);
								if(cell == null||cell.getCellType() != XSSFCell.CELL_TYPE_STRING)continue;
								String key = cell.getStringCellValue();// 标题
								cell = row.getCell(j);
								Object value = null;// 列值
								if(cell!=null){
									if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
										value = cell.getStringCellValue();
									}
									if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
										value = cell.getNumericCellValue();
										if (cell.getDateCellValue() != null) {
											value = cell.getDateCellValue();
										}
									}
									if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
										value = cell.getBooleanCellValue();
									}
								}
								if(value!=null&&!StringUtil.isEmptyStr((String)value)){
									rowvalue = true;
								}
								obj.put(key, value);// 相当对象属性
							}
							if(rowvalue){
								list.add(obj);
							}
						}
					}
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
