package com.mentalhealth.stresstracker.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import org.springframework.stereotype.Service;

// --- THIS IS THE MISSING IMPORT ---
import java.awt.Color; 
// ----------------------------------

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] generateStudentReport(User user, List<Entry> entries) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            // --- Fonts ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(37, 99, 235)); // Blue
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);

            // --- Title Section ---
            Paragraph title = new Paragraph("Mental Health Progress Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph subtitle = new Paragraph("Student Wellness Tracker - Confidential", smallFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(30);
            document.add(subtitle);

            // --- Student Info Section ---
            Paragraph studentInfo = new Paragraph("Student Profile", headerFont);
            studentInfo.setSpacingAfter(10);
            document.add(studentInfo);

            document.add(new Paragraph("Name: " + user.getName(), normalFont));
            document.add(new Paragraph("Student ID: " + (user.getStudentId() != null ? user.getStudentId() : "N/A"), normalFont));
            document.add(new Paragraph("Report Generated: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), normalFont));
            
            Paragraph spacer1 = new Paragraph(" ");
            spacer1.setSpacingAfter(20);
            document.add(spacer1);

            // --- Statistics Section ---
            if (!entries.isEmpty()) {
                double avgMood = entries.stream().mapToInt(Entry::getMood).average().orElse(0.0);
                double avgStress = entries.stream().mapToInt(Entry::getStress).average().orElse(0.0);
                int totalEntries = entries.size();

                Paragraph statsHeader = new Paragraph("Summary Statistics (Last " + totalEntries + " Entries)", headerFont);
                statsHeader.setSpacingAfter(10);
                document.add(statsHeader);

                document.add(new Paragraph(String.format("Average Mood Score: %.1f / 10", avgMood), normalFont));
                document.add(new Paragraph(String.format("Average Stress Score: %.1f / 10", avgStress), normalFont));
                
                Paragraph spacer2 = new Paragraph(" ");
                spacer2.setSpacingAfter(20);
                document.add(spacer2);

                // --- Entries Table Section ---
                Paragraph tableHeader = new Paragraph("Recent Entry History", headerFont);
                tableHeader.setSpacingAfter(10);
                document.add(tableHeader);

                PdfPTable table = new PdfPTable(4); // 4 columns: Date, Mood, Stress, Notes
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2f, 1f, 1f, 4f});

                // Table Headers
                addTableHeader(table, "Date");
                addTableHeader(table, "Mood");
                addTableHeader(table, "Stress");
                addTableHeader(table, "Notes");

                // Table Rows
                for (Entry entry : entries) {
                    table.addCell(new PdfPCell(new Phrase(entry.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), smallFont)));
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(entry.getMood()), smallFont)));
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(entry.getStress()), smallFont)));
                    String notes = entry.getNotes() != null ? entry.getNotes() : "No notes";
                    if (notes.length() > 50) notes = notes.substring(0, 50) + "..."; // Truncate long notes
                    table.addCell(new PdfPCell(new Phrase(notes, smallFont)));
                }

                document.add(table);
            } else {
                document.add(new Paragraph("No entries recorded yet.", normalFont));
            }

            // --- Footer ---
            Paragraph spacer3 = new Paragraph(" ");
            spacer3.setSpacingAfter(30);
            document.add(spacer3);
            
            Paragraph footer = new Paragraph("This report is confidential and intended for the student and authorized counseling staff.", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table, String text) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(new Color(37, 99, 235)); // Blue header
        cell.setPadding(5);
        table.addCell(cell);
    }
}