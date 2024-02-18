package com.example.excelentityimporter.sample.factory;

import com.example.excelentityimporter.helper.BatchUtils;
import com.example.excelentityimporter.importer.ImportingSimpleColumnDefinition;
import com.example.excelentityimporter.importer.ImportingWithRelationColumnDefinition;
import com.example.excelentityimporter.sample.domain.ProjectEntity;
import com.example.excelentityimporter.sample.domain.SampleEntity;
import com.example.excelentityimporter.sample.repository.ProjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SampleImportColumnDefinitions {
    ProjectRepository projectRepository;

    public List<ImportingSimpleColumnDefinition<SampleEntity>> simpleSampleColumnDefinition() {
        return List.of(
                ImportingSimpleColumnDefinition.<SampleEntity>builder()
                        .translateKey("code")
                        .columnNumber(0)
                        .unique(true)
                        .nullable(false)
                        .setFieldValue((sample, value) -> sample.setCode((String) value))
                        .build(),
                ImportingSimpleColumnDefinition.<SampleEntity>builder()
                        .translateKey("removed")
                        .columnNumber(1)
                        .nullable(false)
                        .setFieldValue((sample, value) -> sample.setRemoved(Boolean.parseBoolean((String) value)))
                        .build(),
                ImportingSimpleColumnDefinition.<SampleEntity>builder()
                        .translateKey("registrationDate")
                        .columnNumber(2)
                        .setFieldValue((sample, value) -> {
                            if (value != null) {
                                sample.setRegistrationDate((LocalDateTime) value);
                            } else {
                                sample.setRegistrationDate(null);
                            }
                        })
                        .build()
        );
    }

    public List<ImportingWithRelationColumnDefinition<SampleEntity, ?>> withRelationSampleColumnDefinitions() {
        return List.of(
                ImportingWithRelationColumnDefinition.<SampleEntity, ProjectEntity>builder()
                        .translateKey("project")
                        .columnNumber(3)
                        .unique(false)
                        .setFieldValue((sample, value) -> {
                            if (value != null) {
                                sample.setProjectEntity((ProjectEntity) value);
                            }
                        })
                        .getRelationByIdentifiersMap(codes -> {
                            Set<ProjectEntity> projectsByCodes = projectRepository.getAllByCodeIn(codes);
                            return BatchUtils.itSelfByIdentifierMap(projectsByCodes, ProjectEntity::getCode);
                        })
                        .build()
        );
    }
}
