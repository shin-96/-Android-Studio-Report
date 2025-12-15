package com.example.thuchanhtuan15;

import android.content.Context;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExcelHelper {

    public static boolean exportCustomersToExcel(Context context, ArrayList<Customer> customers, File file) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Khách Hàng");

            // Create styles
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(cellStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle evenRowStyle = workbook.createCellStyle();
            evenRowStyle.cloneStyleFrom(cellStyle);
            evenRowStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Title row
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(25);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DANH SÁCH KHÁCH HÀNG THÂN THIẾT");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            // Date row
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            dateCell.setCellValue("Ngày xuất: " + sdf.format(new Date()));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

            // Summary row
            int totalPoints = 0;
            for (Customer customer : customers) {
                totalPoints += customer.getPoints();
            }
            Row summaryRow = sheet.createRow(2);
            Cell summaryCell = summaryRow.createCell(0);
            summaryCell.setCellValue("Tổng số khách hàng: " + customers.size() + " | Tổng điểm: " + totalPoints);
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            CellStyle summaryStyle = workbook.createCellStyle();
            summaryStyle.setFont(summaryFont);
            summaryCell.setCellStyle(summaryStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));

            // Empty row
            sheet.createRow(3);

            // Header row
            Row headerRow = sheet.createRow(4);
            headerRow.setHeightInPoints(20);

            String[] headers = {"STT", "Số điện thoại", "Điểm", "Ngày cập nhật"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 5;
            for (int i = 0; i < customers.size(); i++) {
                Customer customer = customers.get(i);
                Row row = sheet.createRow(rowNum++);

                CellStyle dataStyle = (i % 2 == 0) ? evenRowStyle : cellStyle;

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(i + 1);
                cell0.setCellStyle(centerStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(customer.getPhone());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(customer.getPoints());
                cell2.setCellStyle(centerStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(customer.getLastUpdated());
                cell3.setCellStyle(dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 6000);
            }

            // Write to file
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Customer> importCustomersFromExcel(File file) {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            FileInputStream inputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Skip header rows (start from row 5, index 5)
            for (int i = 5; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // Read phone (column 1)
                    Cell phoneCell = row.getCell(1);
                    if (phoneCell == null) continue;

                    String phone = "";
                    if (phoneCell.getCellType() == CellType.STRING) {
                        phone = phoneCell.getStringCellValue().trim();
                    } else if (phoneCell.getCellType() == CellType.NUMERIC) {
                        phone = String.valueOf((long) phoneCell.getNumericCellValue());
                    }

                    if (phone.isEmpty()) continue;

                    // Read points (column 2)
                    Cell pointsCell = row.getCell(2);
                    int points = 0;
                    if (pointsCell != null) {
                        if (pointsCell.getCellType() == CellType.NUMERIC) {
                            points = (int) pointsCell.getNumericCellValue();
                        } else if (pointsCell.getCellType() == CellType.STRING) {
                            try {
                                points = Integer.parseInt(pointsCell.getStringCellValue().trim());
                            } catch (NumberFormatException e) {
                                points = 0;
                            }
                        }
                    }

                    // Read last updated (column 3)
                    Cell dateCell = row.getCell(3);
                    String lastUpdated = "";
                    if (dateCell != null && dateCell.getCellType() == CellType.STRING) {
                        lastUpdated = dateCell.getStringCellValue().trim();
                    }

                    // Use current date if no date provided
                    if (lastUpdated.isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        lastUpdated = sdf.format(new Date());
                    }

                    Customer customer = new Customer(0, phone, points, lastUpdated, lastUpdated);
                    customers.add(customer);

                } catch (Exception e) {
                    e.printStackTrace();
                    // Skip invalid rows
                }
            }

            workbook.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }
}