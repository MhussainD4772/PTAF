package com.ptaf.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelWriter {

    private static final Logger logger = Logger.getLogger(ExcelWriter.class.getName());

    public static void writeData(String filePath, String testCaseName, String columnName, String valueToWrite) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                headerRow = sheet.createRow(0);
            }

            int columnIdx = -1;

            // Check if column exists, if not, create it
            for (Cell cell : headerRow) {
                if (cell.toString().trim().equalsIgnoreCase(columnName)) {
                    columnIdx = cell.getColumnIndex();
                    break;
                }
            }

            if (columnIdx == -1) {
                columnIdx = headerRow.getLastCellNum() == -1 ? 0 : headerRow.getLastCellNum();
                Cell newHeaderCell = headerRow.createCell(columnIdx);
                newHeaderCell.setCellValue(columnName);
                logger.info("Created new column: " + columnName);
            }

            boolean isWritten = false;

            for (Row row : sheet) {
                Cell firstCell = row.getCell(0);
                if (firstCell != null && firstCell.toString().trim().equalsIgnoreCase(testCaseName)) {
                    Cell targetCell = row.getCell(columnIdx);
                    if (targetCell == null) {
                        targetCell = row.createCell(columnIdx);
                    }
                    targetCell.setCellValue(valueToWrite); // Overwrite existing value
                    isWritten = true;
                    break;
                }
            }

            if (!isWritten) {
                logger.warning("Test case '" + testCaseName + "' not found.");
                return;
            }

            // Save changes
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            logger.info("Data written successfully.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error writing to Excel file: " + e.getMessage(), e);
        }
    }
}
