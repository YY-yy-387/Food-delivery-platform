package com.sky.utils;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类（基于POI）
 */
public class ExcelUtil {

    private XSSFWorkbook workbook;

    /**
     * 构造方法：基于已有Excel文件创建
     */
    public ExcelUtil(InputStream inputStream) throws IOException {
        workbook = new XSSFWorkbook(inputStream);
        inputStream.close();
    }

    /**
     * 构造方法：创建一个新的Excel文件
     */
    public ExcelUtil() {
        workbook = new XSSFWorkbook();
    }

    /**
     * 获取sheet列表
     */
    public List<String> getSheetNames() {
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheetNames.add(workbook.getSheetName(i));
        }
        return sheetNames;
    }

    /**
     * 获取sheet表数据（从rowIndex开始，colNum列）
     */
    public List<List<String>> getSheetData(int sheetIndex, int startRow, int colNum) {
        XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
        List<List<String>> data = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();
        for (int i = startRow; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<String> rowData = new ArrayList<>();
            for (int j = 0; j < colNum; j++) {
                XSSFCell cell = row.getCell(j);
                rowData.add(cell == null ? "" : cell.toString());
            }
            data.add(rowData);
        }
        return data;
    }

    /**
     * 写入数据到工作簿指定sheet的指定行列
     */
    public void writeDataToSheet(int sheetIndex, int startRow, int startCol, List<List<String>> data) {
        XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
        for (int i = 0; i < data.size(); i++) {
            XSSFRow row = sheet.createRow(startRow + i);
            List<String> rowData = data.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                XSSFCell cell = row.createCell(startCol + j);
                cell.setCellValue(rowData.get(j));
            }
        }
    }

    /**
     * 将工作簿写入输出流
     */
    public void writeToStream(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
        workbook.close();
    }

    /**
     * 获取工作簿对象（用于直接操作生成新报表）
     */
    public XSSFWorkbook getWorkbook() {
        return workbook;
    }
}