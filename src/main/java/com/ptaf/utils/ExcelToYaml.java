package com.ptaf.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ExcelToYaml {
    private static List<Map<String, Object>> data = new ArrayList<>();

    public static void convertExcelToYaml(String testcaseId, String excelFilePath, String yamlFilePath) {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<String> headers = getHeaders(sheet);

            data.clear(); // Clear previous data
            readData(sheet, headers);

            if (testcaseId == null || testcaseId.equalsIgnoreCase("ALL")) {
                List<Map<String, Object>> quotedData = new ArrayList<>();
                for (Map<String, Object> row : data) {
                    quotedData.add(quoteStringValues(row));
                }
                writeDataToYaml(quotedData, yamlFilePath);
                System.out.println("All data has been written to " + yamlFilePath);
            } else {
                Map<String, Object> filteredData = getDataByTestcaseId(testcaseId);
                if (filteredData != null) {
                    Map<String, Object> orderedData = reorderMap(filteredData);
                    Map<String, Object> quotedData = quoteStringValues(orderedData);
                    writeDataToYaml(quotedData, yamlFilePath);
                    System.out.println("Filtered data for '" + testcaseId + "' has been written to " + yamlFilePath);
                } else {
                    System.out.println("Testcase ID '" + testcaseId + "' not found.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getHeaders(Sheet sheet) {
        List<String> headers = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }
        }
        return headers;
    }

    private static void readData(Sheet sheet, List<String> headers) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, Object> rowData = new LinkedHashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                rowData.put(headers.get(j), cell == null ? "" : getCellValue(cell));
            }
            data.add(rowData);
        }
    }

    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double num = cell.getNumericCellValue();
                    return (num == Math.floor(num)) ? (long) num : num;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return null;
        }
    }

    public static Map<String, Object> getDataByTestcaseId(String testcaseId) {
        for (Map<String, Object> row : data) {
            if (row.containsKey("testcase_id") && testcaseId.equals(row.get("testcase_id"))) {
                return row;
            }
        }
        return null;
    }

    private static Map<String, Object> reorderMap(Map<String, Object> original) {
        Map<String, Object> ordered = new LinkedHashMap<>();
        if (original.containsKey("testcase_id")) {
            ordered.put("testcase_id", original.get("testcase_id"));
        }
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            if (!entry.getKey().equals("testcase_id")) {
                ordered.put(entry.getKey(), entry.getValue());
            }
        }
        return ordered;
    }

    private static Map<String, Object> quoteStringValues(Map<String, Object> input) {
        Map<String, Object> quoted = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                quoted.put(entry.getKey(),   value); // No manual quotes
            } else {
                quoted.put(entry.getKey(), value);
            }
        }
        return quoted;
    }


    private static void writeDataToYaml(Object data, String yamlFilePath) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(yamlFilePath)) {
            yaml.dump(data, writer);
        }
    }
}
