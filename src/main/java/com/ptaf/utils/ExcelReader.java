package com.ptaf.utils;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelReader {

    private static final Logger logger = Logger.getLogger(ExcelReader.class.getName());

    public static String getData(String filePath, String testCaseName, String columnName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                logger.warning("The sheet is empty. No rows found.");
                return null;
            }

            // Read header row
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                headerMap.put(cell.toString().trim(), cell.getColumnIndex());
            }

            if (!headerMap.containsKey(columnName)) {
                logger.warning("Column name '" + columnName + "' not found in header row.");
                return null;
            }

            // Find the row with the matching test case name
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell firstCell = row.getCell(0);
                if (firstCell != null && firstCell.toString().trim().equalsIgnoreCase(testCaseName)) {
                    Integer colIndex = headerMap.get(columnName);
                    if (colIndex != null) {
                        Cell targetCell = row.getCell(colIndex);
                        if (targetCell != null) {
                            return targetCell.toString();
                        } else {
                            logger.warning("Target cell is null for test case '" + testCaseName + "' and column '" + columnName + "'.");
                            return null;
                        }
                    } else {
                        logger.warning("Column index for '" + columnName + "' is null.");
                        return null;
                    }
                }
            }

            logger.warning("Test case name '" + testCaseName + "' not found in the sheet.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred while reading Excel file: " + e.getMessage(), e);
        }
        return null;
    }
}
