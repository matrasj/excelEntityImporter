package com.example.excelentityimporter.sample.controller;

import com.example.excelentityimporter.importer.ExcelImporterFacade;
import com.example.excelentityimporter.importer.ExcelTemplateGeneratorFacade;
import com.example.excelentityimporter.sample.domain.SampleEntity;
import com.example.excelentityimporter.sample.factory.SampleImportColumnDefinitions;
import com.example.excelentityimporter.sample.repository.SampleRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/sample")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SampleController {
    SampleRepository sampleRepository;
    SampleImportColumnDefinitions sampleImportColumnDefinitions;
    @PostMapping("/import-template")
    public ResponseEntity<byte[]> downloadExcelTemplate(@RequestBody Map<String, String> translationsMap) {
        XSSFWorkbook workbook = ExcelTemplateGeneratorFacade.generateImportTemplate(
                sampleImportColumnDefinitions.simpleSampleColumnDefinition(),
                sampleImportColumnDefinitions.withRelationSampleColumnDefinitions(),
                translationsMap,
                "Samples"
        );

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "import_template.xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/import-samples")
    @Transactional
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
             ExcelImporterFacade.importEntities(
                     sampleImportColumnDefinitions.simpleSampleColumnDefinition(),
                     sampleImportColumnDefinitions.withRelationSampleColumnDefinitions(),
                     SampleEntity::new,
                     sampleRepository::save,
                     workbook,
                     "Samples"
             );
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
}
