package com.example.excelentityimporter.importer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportingWithRelationColumnDefinition<T, R> {
    String translateKey;
    int columnNumber;
    boolean unique;
    boolean nullable = true;
    BiConsumer<T, Object> setFieldValue;
    Function<Set<String>, Map<String, R>> getRelationByIdentifiersMap;
}
