package com.paisalo.recieptgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnGeneratePDF = findViewById(R.id.btnGeneratePDF);
        btnGeneratePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                    generatePDF();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }

            }
        });
    }

    private void generatePDF() {
        Document document = new Document();

        try {
            String filePath = Environment.getExternalStorageDirectory() + "/receipt.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            addHeader(document);

            // Bill items
            addBillItems(document);

            // Footer with total amount
            addFooter(document);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHeader(Document document) {
        DividerLineEvent event = new DividerLineEvent();
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBorder(PdfPCell.BOX);
        headerCell.addElement(new Phrase("Khushi Traders (Sandeela)", getHeaderFont()));
        headerCell.addElement(new Paragraph("Galla Mandi Sandila Hardoi\nGSTIN/UIN : 09BLHPR2347N1ZG\n" +
                "State Name : Uttar Pradesh, Code: 09"));



        PdfPCell headerCell3 = new PdfPCell();
        headerCell3.setBorder(PdfPCell.BOX);headerCell3.addElement(new Phrase("Bharat Food Product", getHeaderFont()));
        headerCell3.addElement(new Paragraph("Kidwai Nagar, Near Ibrahim Shahid Dargah, \nBilhaur, Kanpur Nagar,\nGSTIN/UIN: 09AIPPA4780L2ZQ\n" +
                "State Name: Uttar Pradesh, Code: 09\n"));
        headerCell3.addElement(new Paragraph("==========================="));
        headerCell3.addElement(new Phrase("Buyer(Bill To)", getBigNormalFont()));
        headerCell3.addElement(new Phrase("Khushi Traders (Sandeela)", getHeaderFont()));
        headerCell3.addElement(new Paragraph("Galla Mandi Sandila Hardoi\nGSTIN/UIN : 09BLHPR2347N1ZG\n" +
                "State Name : Uttar Pradesh, Code: 09"));

        headerTable.addCell(headerCell3);


        PdfPCell headerCell2 = new PdfPCell();
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Add details to the table
        addTableRow(table, "Invoice No.", "18","Dated", "27-Aug-23");
        addTableRow(table, "Delivery Note", "","Mode/Terms of Payment", "");
        addTableRow(table, "Reference No. & Date", "","Other References", "");
        addTableRow(table, "Buyer's Order No.", "","Dated", "");
        addTableRow(table, "Dispatch Doc No.", "","Delivery Note Date", "");
        addTableRow(table, "Dispatched through", "","Destination", "");
        addTableRow(table, "Terms of Delivery", "");
        headerCell2.addElement(table);
        headerTable.addCell(headerCell2);

        // Add more cells with address and contact details

        try {
            document.add(headerTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBillItems(Document document) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setExtendLastRow(true);

        // Add table headers
        addTableCell(table, "Sr No.", true);
        addTableCell(table, "Description of goods", true);
        addTableCell(table, "HSN/SAC", true);
        addTableCell(table, "Quantity", true);
        addTableCell(table, "Rate per", true);
        addTableCell(table, "Amount", true);

        // Add sample data
        addTableCell(table, "1", false);
        addTableCell(table, "Product A", false);
        addTableCell(table, "123456", false);
        addTableCell(table, "10", false);
        addTableCell(table, "$5", false);
        addTableCell(table, "$50", false);

        addTableCell(table, "2", false);
        addTableCell(table, "Product B", false);
        addTableCell(table, "789012", false);
        addTableCell(table, "5", false);
        addTableCell(table, "$8", false);
        addTableCell(table, "$40", false);

        try {
            document.add(table);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void addFooter(Document document) {
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);

        PdfPCell footerCell = new PdfPCell(new Phrase("Total Amount: $30", getFooterFont()));
        footerCell.setBorder(PdfPCell.TOP);
        footerTable.addCell(footerCell);

        try {
            document.add(footerTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Font getHeaderFont() {
        return new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    }

    private Font getNormalFont() {
        return new Font(Font.FontFamily.TIMES_ROMAN, 10);
    }
   private Font getBigNormalFont() {
        return new Font(Font.FontFamily.TIMES_ROMAN, 13);
    }
    private void addTableCell(PdfPTable table, String text, boolean isHeader) {
        Font font = isHeader ? getBoldFont() : getNormalFont();

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(isHeader ? PdfPCell.ALIGN_CENTER : PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(isHeader ? PdfPCell.ALIGN_MIDDLE : PdfPCell.ALIGN_TOP);

        // Set borders
        cell.setBorder(isHeader ? PdfPCell.BOX : PdfPCell.BOX);

        table.addCell(cell);
    }

    private Font getBoldFont() {
        return new Font(Font.FontFamily.TIMES_ROMAN, 11,Font.BOLD);
    }

    private Font getFooterFont() {
        return new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    }
    private void addDividerLine(Document document) {
        Paragraph divider = new Paragraph();
        divider.add(new Phrase(" ", getNormalFont()));

        try {
            document.add(divider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addTextWithDivider(PdfPCell cell, String text, Font font) {
        DividerLineEvent event = new DividerLineEvent();
        PdfPCell textCell = new PdfPCell(new Phrase(text, font));
        textCell.setBorder(PdfPCell.NO_BORDER);
        textCell.setCellEvent(event);
        cell.addElement(textCell);
    }

    private void addTableRow(PdfPTable table, String label1, String value, String label2, String value2) {
        PdfPCell labelCell = new PdfPCell();
        Phrase phrase= new Phrase(label1, getNormalFont());
        Paragraph paragraph= new Paragraph(value, getBoldFont());
        labelCell.addElement(phrase);
        labelCell.addElement(paragraph);
        PdfPCell valueCell = new PdfPCell();
        Phrase phrase2= new Phrase(label2, getNormalFont());
        Paragraph paragraph2= new Paragraph(value2, getBoldFont());
        valueCell.addElement(phrase2);
        valueCell.addElement(paragraph2);
        // Set borders for both label and value cells
        labelCell.setBorder(PdfPCell.BOX);
        valueCell.setBorder(PdfPCell.BOX);

        table.addCell(labelCell);
        table.addCell(valueCell);

    }

    private void addTableRow(PdfPTable table, String label1, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label1, getNormalFont()));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, getNormalFont()));
        labelCell.setFixedHeight(40f);
        valueCell.setFixedHeight(40f);
        // Set borders for both label and value cells
        labelCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setBorder(PdfPCell.NO_BORDER);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

}
class DividerLineEvent implements PdfPCellEvent {
    @Override
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
        float x1 = position.getLeft() + 2;
        float x2 = position.getRight() - 2;
        float y = position.getBottom() + 2;

        PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
        canvas.moveTo(x1, y);
        canvas.lineTo(x2, y);
        canvas.stroke();
    }
}