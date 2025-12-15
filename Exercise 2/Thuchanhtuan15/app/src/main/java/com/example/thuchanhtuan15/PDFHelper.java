package com.example.thuchanhtuan15;

import android.content.Context;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PDFHelper {

    public static boolean exportCustomersToPDF(Context context, ArrayList<Customer> customers, File file) {
        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            Paragraph title = new Paragraph("DANH SACH KHACH HANG THAN THIET")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);

            // Add date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Paragraph date = new Paragraph("Ngay xuat: " + sdf.format(new Date()))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(date);

            // Add summary
            int totalCustomers = customers.size();
            int totalPoints = 0;
            for (Customer customer : customers) {
                totalPoints += customer.getPoints();
            }

            Paragraph summary = new Paragraph(
                    "Tong so khach hang: " + totalCustomers + " | Tong diem: " + totalPoints)
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(15);
            document.add(summary);

            // Create table
            float[] columnWidths = {1, 3, 2, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Table header
            DeviceRgb headerColor = new DeviceRgb(106, 27, 154);

            table.addHeaderCell(new Cell().add(new Paragraph("STT"))
                    .setBackgroundColor(headerColor)
                    .setFontColor(ColorConstants.WHITE)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            table.addHeaderCell(new Cell().add(new Paragraph("So dien thoai"))
                    .setBackgroundColor(headerColor)
                    .setFontColor(ColorConstants.WHITE)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            table.addHeaderCell(new Cell().add(new Paragraph("Diem"))
                    .setBackgroundColor(headerColor)
                    .setFontColor(ColorConstants.WHITE)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            table.addHeaderCell(new Cell().add(new Paragraph("Cap nhat"))
                    .setBackgroundColor(headerColor)
                    .setFontColor(ColorConstants.WHITE)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            // Table rows
            for (int i = 0; i < customers.size(); i++) {
                Customer customer = customers.get(i);

                DeviceRgb rowColor = (i % 2 == 0)
                        ? new DeviceRgb(240, 240, 240)
                        : new DeviceRgb(255, 255, 255); // trắng


                table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1)))
                        .setBackgroundColor(rowColor)
                        .setTextAlignment(TextAlignment.CENTER));

                table.addCell(new Cell().add(new Paragraph(customer.getPhone()))
                        .setBackgroundColor(rowColor));

                table.addCell(new Cell().add(new Paragraph(String.valueOf(customer.getPoints())))
                        .setBackgroundColor(rowColor)
                        .setTextAlignment(TextAlignment.CENTER));

                table.addCell(new Cell().add(new Paragraph(customer.getLastUpdated()))
                        .setBackgroundColor(rowColor)
                        .setFontSize(8));
            }

            document.add(table);

            // Add footer
            Paragraph footer = new Paragraph("\n\nBao cao tu ung dung Khach Hang Than Thiet")
                    .setFontSize(8)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);

            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}