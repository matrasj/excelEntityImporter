package com.example.excelentityimporter.sample.repository;

import com.example.excelentityimporter.sample.domain.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, Long> {
}
