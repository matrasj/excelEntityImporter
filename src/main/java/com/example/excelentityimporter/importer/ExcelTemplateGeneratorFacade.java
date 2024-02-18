package com.example.excelentityimporter.importer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

public class ExcelTemplateGeneratorFacade {
    public static <T> XSSFWorkbook generateImportTemplate(List<ImportingSimpleColumnDefinition<T>> importingSimpleColumnDefinitions,
                                                          List<ImportingWithRelationColumnDefinition<T, ?>> withRelationColumnDefinitions,
                                                          Map<String, String> translationsMap,
                                                          String sheetName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        Row headerRow = sheet.createRow(0);

        for (ImportingSimpleColumnDefinition<T> simpleColumnDefinition : importingSimpleColumnDefinitions) {
            String translateKey = simpleColumnDefinition.getTranslateKey();
            Cell cell = headerRow.createCell(simpleColumnDefinition.getColumnNumber());
            cell.setCellValue(translationsMap.getOrDefault(translateKey, translateKey));
        }

        for (ImportingWithRelationColumnDefinition<T, ?> withRelationColumnDefinition : withRelationColumnDefinitions) {
            String translateKey = withRelationColumnDefinition.getTranslateKey();
            Cell cell = headerRow.createCell(withRelationColumnDefinition.getColumnNumber());
            cell.setCellValue(translationsMap.getOrDefault(translateKey, translateKey));
        }

        return workbook;
    }
}
