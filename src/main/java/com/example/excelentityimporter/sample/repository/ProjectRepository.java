package com.example.excelentityimporter.sample.repository;

import com.example.excelentityimporter.sample.domain.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Set<ProjectEntity> getAllByCodeIn(Set<String> uniqueCodes);
}
