package com.example.excelentityimporter.importer;

import com.example.excelentityimporter.sample.domain.SampleEntity;
import org.apache.commons.compress.utils.Sets;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ExcelImporterFacade {

    public static<T> void importEntities(List<ImportingSimpleColumnDefinition<T>> importingSimpleColumnDefinitions,
                                         List<ImportingWithRelationColumnDefinition<T, ?>> withRelationColumnDefinitions,
                                         Supplier<T> createEntityFunction,
                                         Consumer<T> saveEntityFunction,
                                         XSSFWorkbook workbook,
                                         String sheetName) {
        XSSFSheet importingSheet = workbook.getSheet(sheetName);
        int lastRowNum = importingSheet.getLastRowNum();

        Map<Integer, Map<String, ?>> cachesForColumnNumber = new HashMap<>();

        // Prepare caches
        for (ImportingWithRelationColumnDefinition<T, ?> importingWithRelationColumnDefinition: withRelationColumnDefinitions) {
            Set<String> uniqueIdentifier = Sets.newHashSet();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = importingSheet.getRow(i);
                if (row != null) {
                    XSSFCell cell = row.getCell(importingWithRelationColumnDefinition.getColumnNumber());
                    if (cell != null && cell.getStringCellValue() != null && !cell.getStringCellValue().isEmpty()) {
                        uniqueIdentifier.add(cell.getStringCellValue());
                    }
                }
            }

            Map<String, ?> itSelfByIdentifierMap = importingWithRelationColumnDefinition.getGetRelationByIdentifiersMap().apply(uniqueIdentifier);
            cachesForColumnNumber.put(importingWithRelationColumnDefinition.getColumnNumber(), itSelfByIdentifierMap);
        }


        for (int i = 1; i <= lastRowNum; i++) {
            XSSFRow row = importingSheet.getRow(i);
            if (row != null) {
                T newEntity = createEntityFunction.get();
                for (ImportingSimpleColumnDefinition<T> simpleColumn: importingSimpleColumnDefinitions) {
                    XSSFCell cell = row.getCell(simpleColumn.getColumnNumber());
                    simpleColumn.getSetFieldValue().accept(newEntity, extractDataFromCell(cell));
                }

                for (ImportingWithRelationColumnDefinition<T, ?> witRelationColumn: withRelationColumnDefinitions) {
                    XSSFCell cell = row.getCell(witRelationColumn.getColumnNumber());
                    witRelationColumn.getSetFieldValue().accept(
                            newEntity,
                            cachesForColumnNumber.get(witRelationColumn.getColumnNumber()).getOrDefault(cell.getStringCellValue(), null)
                    );
                }

                saveEntityFunction.accept(newEntity);
            }
        }
    }

    private static Object extractDataFromCell(XSSFCell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? LocalDateTime
                    .ofInstant(cell.getDateCellValue().toInstant(), ZoneId.systemDefault()) : cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            default -> null;
        };
    }
}